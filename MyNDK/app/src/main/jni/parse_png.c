//
// Created by 333 on 2016/7/21.
//
#include "parse_png.h"
#include "Png/png.h"

//----------------------------------------------------------------------------------------------------------------
static void pngReaderCallback(png_structp png_ptr, png_bytep data, png_size_t length)
{
    ImageSource* isource = (ImageSource*)png_get_io_ptr(png_ptr);
    if(isource->offset + length <= isource->size)
    {
        memcpy(data, isource->data + isource->offset, length);
        isource->offset += length;
    }
    else
    {
        png_error(png_ptr,"pngReaderCallback failed");
    }
}

//----------------------------------------------------------------------------------------------------------------

void decodePNGFromStream(ImageInfo *imageInfo,const unsigned char* pixelData, const unsigned int dataSize)
{
png_structp png_ptr;
png_infop info_ptr;
int width, height, rowBytes;
png_byte color_type;  //可以是PNG_COLOR_TYPE_RGB,PNG_COLOR_TYPE_PALETTE.......等
png_byte bit_depth;
png_colorp palette;

png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL,NULL,NULL);
if (!png_ptr)
{
png_destroy_read_struct(&png_ptr, &info_ptr, (png_info*)NULL);
//TFC_DEBUG("ReadPngFile: Failed to create png_ptr");
}
info_ptr = png_create_info_struct(png_ptr);
if (!info_ptr)
{
png_destroy_read_struct(&png_ptr, &info_ptr, (png_info*)NULL);
//TFC_DEBUG("ReadPngFile: Failed to create info_ptr");
}
if (setjmp(png_jmpbuf(png_ptr)))
{
png_destroy_read_struct(&png_ptr, &info_ptr, (png_info*)NULL);
//TFC_DEBUG("ReadPngFile: Failed to read the PNG file");
}

ImageSource imgsource;
imgsource.data = (unsigned char*)pixelData;
imgsource.size = dataSize;
imgsource.offset = 0;
//define our own callback function for I/O instead of reading from a file
png_set_read_fn(png_ptr,&imgsource, pngReaderCallback);

/* **************************************************
 * The low-level read interface in libpng (http://www.libpng.org/pub/png/libpng-1.2.5-manual.html)
 * **************************************************
 */
png_read_info(png_ptr, info_ptr);
width = png_get_image_width(png_ptr, info_ptr);
height = png_get_image_height(png_ptr, info_ptr);
color_type = png_get_color_type(png_ptr, info_ptr);
bit_depth = png_get_bit_depth(png_ptr, info_ptr);
rowBytes = png_get_rowbytes(png_ptr, info_ptr);
 int channels =    png_get_channels(png_ptr, info_ptr);
// Convert stuff to appropriate formats!
if(color_type==PNG_COLOR_TYPE_PALETTE)
{
png_set_packing(png_ptr);
png_set_palette_to_rgb(png_ptr); //Expand data to 24-bit RGB or 32-bit RGBA if alpha available.
}
if (color_type == PNG_COLOR_TYPE_GRAY && bit_depth < 8);
//png_set_extern_gray_1_2_4_to_8(png_ptr);
if (color_type == PNG_COLOR_TYPE_GRAY_ALPHA)
png_set_gray_to_rgb(png_ptr);
if (bit_depth == 16)
png_set_strip_16(png_ptr);

//Expand paletted or RGB images with transparency to full alpha channels so the data will be available as RGBA quartets.
if(png_get_valid(png_ptr, info_ptr, PNG_INFO_tRNS))
{
png_set_tRNS_to_alpha(png_ptr);
}
//png_read_update_info(png_ptr, info_ptr);
unsigned char* rgba = (unsigned char*)malloc(width * height * 4);  //each pixel(RGBA) has 4 bytes
               png_bytep * row_pointers;
               row_pointers = (png_bytep*)malloc(sizeof(png_bytep) * height);
for (int y = 0; y < height; y++)
{
row_pointers[y] = (png_bytep)malloc(width<<2); //each pixel(RGBA) has 4 bytes
}
png_read_image(png_ptr, row_pointers);

//unlike store the pixel data from top-left corner, store them from bottom-left corner for OGLES Texture drawing...
int pos = (width * height * 4) - (4 * width);
for(int row = 0; row < height; row++)
{
for(int col = 0; col < (4 * width); col += 4)
{
rgba[pos++] = row_pointers[row][col];        // red
rgba[pos++] = row_pointers[row][col + 1];    // green
rgba[pos++] = row_pointers[row][col + 2];    // blue
rgba[pos++] = row_pointers[row][col + 3];    // alpha
}
pos=(pos - (width * 4)*2); //move the pointer back two rows
}

imageInfo->pixelData = rgba;
imageInfo->imageHeight = height;
imageInfo->imageWidth = width;

//clean up after the read, and free any memory allocated
png_destroy_read_struct(&png_ptr, &info_ptr, NULL);
}