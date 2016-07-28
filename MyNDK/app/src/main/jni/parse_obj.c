//
// Created by 333 on 2016/7/12.
//

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <math.h>
#include "parse_obj.h"
#define g_v(x) (group->index_vertices)[x]
#define g_t(x) (group->index_texcoords)[x]
#define g_n(x) (group->index_normals)[x]
/* strdup is actually not a standard ANSI C or POSIX routine
   so implement a private one for GLM.  OpenVMS does not have a
   strdup; Linux's standard libc doesn't declare strdup by default
   (unless BSD or SVID interfaces are requested). */
char * __glmStrdup(const char *string)
{
	char *copy = (char*)malloc(strlen(string) + 1);
	if (copy == NULL)
		return NULL;
	strcpy(copy, string);
	return copy;
}

/* strip leading and trailing whitespace from a string and return a newly
   allocated string containing the result (or NULL if the string is only
   whitespace)*/
char * __glmStrStrip(const char *s)
{
	int first;
	int last = strlen(s) - 1;
	int len;
	int i;
	char * rets;

	i = 0;
	while (i <= last &&
		(s[i] == ' ' || s[i] == '\t' || s[i] == '\n' || s[i] == '\r'))
		i++;
	if (i > last)
		return NULL;
	first = i;
	i = last;
	while (i > first &&
		(s[i] == ' ' || s[i] == '\t' || s[i] == '\n' || s[i] == '\r'))
		i--;
	last = i;
	len = last - first + 1;
	rets = (char*)malloc(len + 1); /* add a trailing 0 */
	memcpy(rets, s + first, len);
	rets[len] = 0;
	return rets;
}

void __glmWarning(char *format, ...)
{
	va_list args;

	va_start(args, format);
	fprintf(stderr, "GLM: Warning: ");
	vfprintf(stderr, format, args);
	va_end(args);
	putc('\n', stderr);
}

void __glmFatalError(char *format, ...)
{
	va_list args;
	va_start(args, format);
	fprintf(stderr, "GLM: Fatal Error: ");
	vfprintf(stderr, format, args);
	va_end(args);
	putc('\n', stderr);
	exit(1);
}




/* glmCross: compute the cross product of two vectors
 *
 * u - array of 3 GLfloats (GLfloat u[3])
 * v - array of 3 GLfloats (GLfloat v[3])
 * n - array of 3 GLfloats (GLfloat n[3]) to return the cross product in
 */
static GLvoid
glmCross(GLfloat* u, GLfloat* v, GLfloat* n)
{
	assert(u); assert(v); assert(n);

	n[0] = u[1] * v[2] - u[2] * v[1];
	n[1] = u[2] * v[0] - u[0] * v[2];
	n[2] = u[0] * v[1] - u[1] * v[0];
}


/* glmNormalize: normalize a vector
 *
 * v - array of 3 GLfloats (GLfloat v[3]) to be normalized
 */
static GLvoid glmNormalize(GLfloat* v)
{

}


/* glmFacetNormals: Generates facet normals for a model (by taking the
 * cross product of the two vectors derived from the sides of each
 * triangle).  Assumes a counter-clockwise winding.
 *
 * model - initialized GLMmodel structure
 */
GLvoid glmFacetNormals(GLfloat* a, GLfloat* b, GLfloat* c)
{
	GLfloat u[3];
	GLfloat v[3];
	GLint i = 0;
	GLfloat tmp;
	for (; i < 3; i++)
	{
		u[i] = a[i] - b[i];
		v[i] = a[i] - c[i];
	}
	glmCross(u, v, c);

	tmp = (GLfloat)sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);
	c[0] /= tmp;
	c[1] /= tmp;
	c[2] /= tmp;
}

/* glmFindGroup: Find a group in the model */
static GLMgroup* glmFindGroup(GLMmodel* model, char* name)
{
	GLMgroup* group;

	assert(model);

	group = model->groups;
	while (group) {
		if (!strcmp(name, group->name))
			break;
		group = group->next;
	}
	return group;
}

/* glmAddGroup: Add a group to the model */
static GLMgroup* glmAddGroup(GLMmodel* model, char* name)
{
	GLMgroup* group;

	group = glmFindGroup(model, name);
	if (!group) {
		group = (GLMgroup *) malloc(sizeof(GLMgroup));
		group->name = __glmStrdup(name);
		group->numtriangles = 0;
		group->index_vertices = NULL;
		group->index_normals = NULL;
		group->index_texcoords = NULL;
		group->face_noraml = NULL;
		group->pmaterial = NULL;
		group->next = NULL;
	}
		if(model->groups == NULL) {
			model->groups = group;
		}else
		{
			GLMgroup* group_tmp = model->groups;
			while(group_tmp->next)
			{group_tmp = group_tmp->next;}
			group_tmp->next = group;
	}
	model->numgroups++;
	return group;
}


/* glmFindGroup: Find a material in the model */
static GLuint glmFindMaterial(GLMmodel* model, char* name)
{
	GLuint i;

	assert(name != NULL);
	/* XXX doing a linear search on a string key'd list is pretty lame,
	but it works and is fast enough for now. */
	for (i = 0; i < model->nummaterials; i++) {
		assert(model->materials[i].name != NULL);
		if (!strcmp(model->materials[i].name, name))
			goto found;
	}

	/* didn't find the name, so print a warning and return the default
	material (0). */
	__glmWarning("glmFindMaterial():  can't find material \"%s\".", name);
	i = 0;

found:
	return i;
}

/* glmFirstPass: first pass at a Wavefront OBJ file that gets all the
 * statistics of the model (such as #vertices, #normals, etc)
 *
 * model - properly initialized GLMmodel structure
 * file  - (fopen'd) file descriptor
 */
GLvoid  glmFirstPass(GLMmodel* model, char* buf, long buf_size)
{
	GLuint  numvertices;        /* number of vertices in model */
	GLuint  numnormals;         /* number of normals in model */
	GLuint  numtexcoords;       /* number of texcoords in model */
	GLuint  numtriangles;       /* number of triangles in model */
	GLMgroup* group;            /* current group */
	unsigned    v, n, t;
	long i = 0;
	/* make a default group */

	numvertices = numnormals = numtexcoords = numtriangles = 0;
	do
	{
		switch (buf[i])
		{
		case '#':               /* comment */
			/* eat up rest of line */
			while (buf[++i] != '\n');
			break;

		case 'v':               /* v, vn, vt */
			switch (buf[++i])
			{
			case ' ':          /* vertex */
				/* eat up rest of line */
				while (buf[++i] != '\n');
				numvertices++;
				break;
			case 'n':           /* normal */
				/* eat up rest of line */
				while (buf[++i] != '\n');
				numnormals++;
				break;
			case 't':           /* texcoord */
				/* eat up rest of line */
				while (buf[++i] != '\n');
				numtexcoords++;
				break;
			default:
				__glmFatalError("glmFirstPass(): Unknown token \"%s\".", buf + i);
				break;
			}
			break;

		case 'm':
			if (strncmp(buf + i, "mtllib", 6) != 0)
				__glmFatalError("glmReadOBJ: Got \"%s\" instead of \"mtllib\"", buf + i);
			i += 6;     //skip mtllib and one space
			while (buf[++i] == ' ');
			char * name_tmp = (char*)malloc(100);
			int j = 0;
			do
			{
				name_tmp[j++] = buf[i];
			} while (buf[++i] != '\n' && buf[i] != ' ');
			name_tmp[j] = '\0';

			model->mtllibname = __glmStrStrip((char*)name_tmp);
			model->nummaterials++;
			free(name_tmp);
			name_tmp == NULL;
			//glmReadMTL(model, model->mtllibname);
			if (buf[i] == ' ')
				/* eat up rest of line */
			while (buf[++i] != '\n');
			break;

		case 'u':
			if (strncmp(buf + i, "usemtl", 6) != 0)
				__glmFatalError("glmReadOBJ: Got \"%s\" instead of \"usemtl\"", buf + i);
			/* eat up rest of line */
			while (buf[++i] != '\n');
			break;

		case 'g':               /* group */
			if(buf[i+1] == 'r')  // it is a group
			{
				i += 4;
				while (buf[++i] == ' ');
				char * tmp_name = (char*)malloc(100);
				int j = 0;
				do
				{
					tmp_name[j++] = buf[i];
				} while (buf[++i] != '\n');
				tmp_name[j] = '\0';
				group = glmAddGroup(model, tmp_name);
				free(tmp_name);
				tmp_name = NULL;
			}
				else
				{
					// g end
					/* eat up rest of line */
					while (buf[++i] != '\n');
				}
			break;

		case 'f':               /* face */
			/* can be one of %d, %d//%d, %d/%d, %d/%d/%d*/
			while (buf[i] == 'f')
			{
				/* eat up rest of line */
				while (buf[++i] != '\n');
				numtriangles++;
				group->numtriangles++;
				++i;
			}
			--i;
			break;
		case '\0':
			break;
			case '\n':
				break;
		default:
			/* eat up rest of line */
			while (buf[++i] != '\n');
			break;
		}
	} while (i++ < buf_size);

	/* set the stats in the model structure */
	model->numvertices = numvertices;
	model->numnormals = numnormals;
	model->numtexcoords = numtexcoords;
	model->numtriangles = numtriangles;

	/* allocate memory for the triangles in each group */
	group = model->groups;
	while (group) {
		group->index_vertices = (GLuint*)malloc(sizeof(GLuint)* group->numtriangles * 3);
		group->index_normals = (GLuint*)malloc(sizeof(GLuint)* group->numtriangles * 3);
		group->index_texcoords = (GLuint*)malloc(sizeof(GLuint)* group->numtriangles * 3);
		group->face_noraml = (GLfloat*)malloc(sizeof(GLfloat)*group->numtriangles * 3);
		group = group->next;
	}
}


/* glmSecondPass: second pass at a Wavefront OBJ file that gets all
* the data.
*
* model - properly initialized GLMmodel structure
* file  - (fopen'd) file descriptor
*/
GLvoid glmSecondPass(GLMmodel* model, const char* buf, long buf_size)
{
	GLuint  numvertices;        /* number of vertices in model */
	GLuint  numnormals;         /* number of normals in model */
	GLuint  numtexcoords;       /* number of texcoords in model */
	GLuint  numtriangles;       /* number of triangles in model */
	GLfloat*    vertices;           /* array of vertices  */
	GLfloat*    normals;            /* array of normals */
	GLfloat*    texcoords;          /* array of texture coordinates */
	GLMgroup* group;            /* current group pointer */

	unsigned int v, t, n;

	/* set the pointer shortcuts */
	vertices = model->vertices;
	if (model->normals)
		normals = model->normals;
	if (model->texcoords)
		texcoords = model->texcoords;

	/* on the second pass through the file, read all the data into the
	allocated arrays */
	numvertices = numnormals = numtexcoords = 1;
	numtriangles = 0;
	int i = 0;
	do{
		switch (buf[i])
		{
		case '#':               /* comment */
			/* eat up rest of line */
			while (buf[++i] != '\n');
			break;

		case 'v':               /* v, vn, vt */
			switch (buf[++i])
			{
			case ' ':          /* vertex */
				++i;           // skip space
				sscanf(buf + i, "%f %f %f",
					&vertices[3 * numvertices + 0],
					&vertices[3 * numvertices + 1],
					&vertices[3 * numvertices + 2]);
				numvertices++;
				/* eat up rest of line */
				while (buf[++i] != '\n');
				break;
			case 'n':           /* normal */
				++i;            // skip 'n'
				sscanf(buf + i, "%f %f %f",
					&normals[3 * numnormals + 0],
					&normals[3 * numnormals + 1],
					&normals[3 * numnormals + 2]);
				numnormals++;
				/* eat up rest of line */
				while (buf[++i] != '\n');
				break;
			case 't':           /* texcoord */
				++i;			//skip 't'
				sscanf(buf + i, "%f %f",
					&texcoords[2 * numtexcoords + 0],
					&texcoords[2 * numtexcoords + 1]);
				numtexcoords++;
				/* eat up rest of line */
				while (buf[++i] != '\n');
				break;
			}
			break;

		case 'u':
		{
					char *tmp_1 = (char *)malloc(100);
					char *tmp_2 = (char *)malloc(100);
					sscanf(buf + i, "%s %s", tmp_1, tmp_2);
					//material = glmFindMaterial(model, tmp_2);

					free(tmp_1);
					free(tmp_2);
					tmp_1 = NULL;
					tmp_2 = NULL;
		}
			/* eat up rest of line */
			while (buf[++i] != '\n');
			break;

		case 'g':               /* group */
			if(buf[i+1] == 'r')  // it is a group
			{
				i += 4;
				while (buf[++i] == ' ');
				char * tmp_name = (char*)malloc(100);
				int j = 0;
				do
				{
					tmp_name[j++] = buf[i];
				} while (buf[++i] != '\n');
				tmp_name[j] = '\0';
				group = glmFindGroup(model, tmp_name);
				free(tmp_name);
				tmp_name = NULL;
			}
				else{
				/* eat up rest of line */
				while (buf[++i] != '\n');
			}
			break;

		case 'f':               /* face */
			v = t = n = 0;
			int tmp = i;       // save the current i,and check the type of f,
			int type = 0;      //the type of f:%d,  %d//%d, %d/%d, %d/%d/%d
			++tmp;              //skip f
			while (buf[++tmp] == ' '); // skip space after f
			while (buf[tmp] != '/' && buf[tmp] != ' ')++tmp;
			if (buf[tmp] == ' ')
			{
				type = 1;          // %d
			}
			else if (buf[tmp] == '/')
			{
				if (buf[++tmp] == '/')  // %d//%d
				{
					type = 2;// %d//%d
				}
				else
				{
					while (buf[tmp] != '/' && buf[tmp] != ' ')++tmp;
					if (buf[tmp] == ' ')
						type = 3;             //    %d/%d
					else
						type = 4;              // %d/%d/%d
				}
			}
			tmp = 0;
			float  a[3], b[3], c[3];
			while (buf[i] == 'f')
			{
				++i; //skip f;
				while (buf[++i] == ' ');//skip space
				switch (type)
				{
				case 1:
					/* v */
					sscanf(buf + i, "%u", &v);
					g_v(tmp) = v;
					g_t(tmp) = -1;
					g_n(tmp) = -1;
					memcpy(a, model->vertices + v * 3, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u", &v);
					g_v(tmp + 1) = v;
					g_t(tmp + 1) = -1;
					g_n(tmp + 1) = -1;
					memcpy(b, model->vertices + v * 3, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u", &v);
					g_v(tmp + 2) = v;
					g_t(tmp + 2) = -1;
					g_n(tmp + 2) = -1;
					memcpy(c, model->vertices + v * 3, sizeof(float)* 3);
					glmFacetNormals(a, b, c);
					memcpy(group->face_noraml + tmp, c, sizeof(float)* 3);
					tmp += 3;
					numtriangles++;
					while (buf[++i] != '\n');

					break;
				case 2:
					/* v//n */
					sscanf(buf + i, "%u//%u", &v, &n);
					g_v(tmp) = v;
					g_t(tmp) = -1;
					g_n(tmp) = n;
					memcpy(a, model->vertices + v, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u//%u", &v, &n);
					g_v(tmp + 1) = v;
					g_t(tmp + 1) = -1;
					g_n(tmp + 1) = n;
					memcpy(b, model->vertices + v, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u//%u", &v, &n);
					g_v(tmp + 2) = v;
					g_t(tmp + 2) = -1;
					g_n(tmp + 2) = n;
					memcpy(c, model->vertices + v, sizeof(float)* 3);
					glmFacetNormals(a, b, c);
					memcpy(group->face_noraml + tmp, c, sizeof(float)* 3);
					tmp += 3;
					numtriangles++;
					while (buf[++i] != '\n');

					break;
				case 3:
					/* v/t */
					sscanf(buf + i, "%u/%u", &v, &t);
					g_v(tmp) = v;
					g_t(tmp) = t;
					g_n(tmp) = -1;
					memcpy(a, model->vertices + v, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u/%u", &v, &t);
					g_v(tmp + 1) = v;
					g_t(tmp + 1) = t;
					g_n(tmp + 1) = -1;
					memcpy(b, model->vertices + v, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u/%u", &v, &t);
					g_v(tmp + 2) = v;
					g_t(tmp + 2) = t;
					g_n(tmp + 2) = -1;
					memcpy(c, model->vertices + v, sizeof(float)* 3);
					glmFacetNormals(a, b, c);
					memcpy(group->face_noraml + tmp, c, sizeof(float)* 3);
					tmp += 3;
					numtriangles++;
					while (buf[++i] != '\n');

					break;
				case 4:
					/* v/t/n */
					sscanf(buf + i, "%u/%u/%u", &v, &t, &n);
					g_v(tmp) = v;
					g_t(tmp) = t;
					g_n(tmp) = n;
					memcpy(a, model->vertices + v, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u/%u/%u", &v, &t, &n);
					g_v(tmp + 1) = v;
					g_t(tmp + 1) = t;
					g_n(tmp + 1) = n;
					memcpy(b, model->vertices + v, sizeof(float)* 3);
					while (buf[++i] != ' ');
					while (buf[++i] == ' ');
					sscanf(buf + i, "%u/%u/%u", &v, &t, &n);
					g_v(tmp + 2) = v;
					g_t(tmp + 2) = t;
					g_n(tmp + 2) = n;
					glmFacetNormals(a, b, c);
					memcpy(group->face_noraml + tmp, c, sizeof(float)* 3);
					tmp += 3;
					numtriangles++;
					while (buf[++i] != '\n');

					break;
				default:
					break;
				}
				++i;
			}
			--i;
			break;
		case '\0':
			break;
			case '\n':
				break;
		default:
			/* eat up rest of line */
			while (buf[++i] != '\n');
			break;
		}
	} while (i++ < buf_size);
}

GLvoid 	glmThirdPass(GLMmodel*model)
{
	GLfloat *vertex = model->vertices;
	GLfloat * ver = (GLfloat*)malloc(sizeof(GLfloat)*(model->numtexcoords+1)*3);
	GLMgroup* group = model->groups;
	while (group) {
		int i,source_addr,des_addr;
		for(i = 0;i<group->numtriangles*3;i++) {
			source_addr = (group->index_vertices[i])*3;
			des_addr = (group->index_texcoords[i]) *3;
			memcpy(ver +des_addr , vertex +source_addr , sizeof(GLfloat) * 3);
		}
		group = group->next;

	}
	free(model->vertices);
	model->vertices = ver;
}
#if 0
/* glmReadMTL: read a wavefront material library file
*
* model - properly initialized GLMmodel structure
* name  - name of the material library
*/
static GLvoid  glmReadMTL(GLMmodel* model, char* name)
{
	FILE* file;
	char* dir;
	char* filename;
	char* tex_filename;
	char* t_filename;
	char    buf[128];
	GLuint nummaterials, i;

	dir = __glmDirName(model->pathname);
	filename = (char*)malloc(sizeof(char)* (strlen(dir) + strlen(name) + 1));
	strcpy(filename, dir);
	strcat(filename, name);

	file = fopen(filename, "r");
	if (!file) {
		__glmFatalError("glmReadMTL() failed: can't open material file \"%s\".",
			filename);
	}

	/* count the number of materials in the file */
	nummaterials = 1;
	while (fscanf(file, "%s", buf) != EOF) {
		switch (buf[0]) {
		case '#':               /* comment */
			/* eat up rest of line */
			fgets(buf, sizeof(buf), file);
			break;
		case 'n':               /* newmtl */
			if (strncmp(buf, "newmtl", 6) != 0)
				__glmFatalError("glmReadMTL: Got \"%s\" instead of \"newmtl\" in file \"%s\"", buf, filename);
			fgets(buf, sizeof(buf), file);
			nummaterials++;
			sscanf(buf, "%s %s", buf, buf);
			break;
		default:
			/* eat up rest of line */
			fgets(buf, sizeof(buf), file);
			break;
		}
	}

	rewind(file);

	model->materials = (GLMmaterial*)malloc(sizeof(GLMmaterial)* nummaterials);
	model->nummaterials = nummaterials;

	/* set the default material */
	for (i = 0; i < nummaterials; i++) {
		model->materials[i].name = NULL;
		model->materials[i].shininess = 65.0;
		model->materials[i].diffuse[0] = 0.8;
		model->materials[i].diffuse[1] = 0.8;
		model->materials[i].diffuse[2] = 0.8;
		model->materials[i].diffuse[3] = 1.0;
		model->materials[i].ambient[0] = 0.2;
		model->materials[i].ambient[1] = 0.2;
		model->materials[i].ambient[2] = 0.2;
		model->materials[i].ambient[3] = 1.0;
		model->materials[i].specular[0] = 0.0;
		model->materials[i].specular[1] = 0.0;
		model->materials[i].specular[2] = 0.0;
		model->materials[i].specular[3] = 1.0;
		model->materials[i].map_diffuse = -1;
	}
	model->materials[0].name = __glmStrdup("default");

	/* now, read in the data */
	nummaterials = 0;
	while (fscanf(file, "%s", buf) != EOF) {
		switch (buf[0]) {
		case '#':               /* comment */
			/* eat up rest of line */
			fgets(buf, sizeof(buf), file);
			break;
		case 'n':               /* newmtl */
#if 0
			__glmWarning("name=%s; Ns=%g; Ka=%g,%g,%g; Kd=%g,%g,%g; Ks=%g,%g,%g",
				model->materials[nummaterials].name,
				model->materials[nummaterials].shininess / 128.0*GLM_MAX_SHININESS,
				model->materials[nummaterials].ambient[0],
				model->materials[nummaterials].ambient[1],
				model->materials[nummaterials].ambient[2],
				model->materials[nummaterials].diffuse[0],
				model->materials[nummaterials].diffuse[1],
				model->materials[nummaterials].diffuse[2],
				model->materials[nummaterials].specular[0],
				model->materials[nummaterials].specular[1],
				model->materials[nummaterials].specular[2]);
#endif
			if (strncmp(buf, "newmtl", 6) != 0)
				__glmFatalError("glmReadMTL: Got \"%s\" instead of \"newmtl\" in file \"%s\"", buf, filename);
			fgets(buf, sizeof(buf), file);
			sscanf(buf, "%s %s", buf, buf);
			nummaterials++;
			model->materials[nummaterials].name = __glmStrdup(buf);
			break;
		case 'N':
			switch (buf[1]) {
			case 's':
				fscanf(file, "%f", &model->materials[nummaterials].shininess);
				/* wavefront shininess is from [0, 1000], so scale for OpenGL */
				model->materials[nummaterials].shininess /= GLM_MAX_SHININESS;
				model->materials[nummaterials].shininess *= 128.0;
				break;
			case 'i':
				/* Refraction index.  Values range from 1 upwards. A value
				of 1 will cause no refraction. A higher value implies
				refraction. */
				__glmWarning("refraction index ignored");
				fgets(buf, sizeof(buf), file);
				break;
			default:
				__glmWarning("glmReadMTL: Command \"%s\" ignored", buf);
				fgets(buf, sizeof(buf), file);
				break;
			}
			break;
		case 'K':
			switch (buf[1]) {
			case 'd':
				fscanf(file, "%f %f %f",
					&model->materials[nummaterials].diffuse[0],
					&model->materials[nummaterials].diffuse[1],
					&model->materials[nummaterials].diffuse[2]);
				break;
			case 's':
				fscanf(file, "%f %f %f",
					&model->materials[nummaterials].specular[0],
					&model->materials[nummaterials].specular[1],
					&model->materials[nummaterials].specular[2]);
				break;
			case 'a':
				fscanf(file, "%f %f %f",
					&model->materials[nummaterials].ambient[0],
					&model->materials[nummaterials].ambient[1],
					&model->materials[nummaterials].ambient[2]);
				break;
			default:
				__glmWarning("glmReadMTL: Command \"%s\" ignored", buf);
				/* eat up rest of line */
				fgets(buf, sizeof(buf), file);
				break;
			}
			break;
		case 'd':
			/* d = Dissolve factor (pseudo-transparency).
			Values are from 0-1. 0 is completely transparent, 1 is opaque. */
		{
					float alpha;
					fscanf(file, "%f", &alpha);
					model->materials[nummaterials].diffuse[3] = alpha;
		}
			break;
		case 'i':
			if (strncmp(buf, "illum", 5) != 0)
				__glmFatalError("glmReadMTL: Got \"%s\" instead of \"illum\" in file \"%s\"", buf, filename);
			/* illum = (0, 1, or 2) 0 to disable lighting, 1 for
			ambient & diffuse only (specular color set to black), 2
			for full lighting. I've also seen values of 3 and 4 for
			'illum'... when there's a 3 there, there's often a
			'sharpness' attribute, but I didn't find any
			explanation. And I think the 4 illum value is supposed
			to denote two-sided polygons, but I kinda get the
			impression that some people just make stuff up and add
			whatever they want to these files, so there could be
			anything in there ;). */
			{
				int illum;
				fscanf(file, "%d", &illum);
				if (illum != 2)	/* illum=2 is standard lighting */
					__glmWarning("illum material ignored: illum %d", illum);
			}
			break;
		case 'm':
			/* texture map */
			tex_filename = malloc(FILENAME_MAX);
			fgets(tex_filename, FILENAME_MAX, file);
			t_filename = __glmStrStrip(tex_filename);
			free(tex_filename);
			if (strncmp(buf, "map_Kd", 6) == 0) {
				model->materials[nummaterials].map_diffuse = glmFindOrAddTexture(model, t_filename);
				free(t_filename);
			}
			else {
				__glmWarning("map %s %s ignored", buf, t_filename);
				free(t_filename);
				fgets(buf, sizeof(buf), file);
			}
			break;
		case 'r':
			/* reflection type and filename (?) */
			fgets(buf, sizeof(buf), file);
			__glmWarning("reflection type ignored: r%s", buf);
			break;
		default:
			/* eat up rest of line */
			fgets(buf, sizeof(buf), file);
			break;
		}
	}
	free(dir);
	fclose(file);
	free(filename);
}
#endif
/* glmReadOBJ: Reads a model description from a Wavefront .OBJ file.
 * Returns a pointer to the created object which should be free'd with
 * glmDelete().
 *
 * filename - name of the file containing the Wavefront .OBJ format data.
 */
GLMmodel* glmReadOBJ(const char* buf, long size, char* filename)
{
	/* allocate a new model */
	GLMmodel* model = (GLMmodel*)malloc(sizeof(GLMmodel));
	model->pathname = __glmStrdup(filename);
	model->mtllibname = NULL;
	model->numvertices = 0;
	model->vertices = NULL;
	model->numnormals = 0;
	model->normals = NULL;
	model->numtexcoords = 0;
	model->texcoords = NULL;
	model->numtriangles = 0;
	model->nummaterials = 0;
	model->materials = NULL;
	model->numgroups = 0;
	model->groups = NULL;
	model->position[0] = 0.0;
	model->position[1] = 0.0;
	model->position[2] = 0.0;

	/* make a first pass through the file to get a count of the number
	   of vertices, normals, texcoords & triangles */
	glmFirstPass(model, buf, size);

	/* allocate memory */
	model->vertices = (GLfloat*)malloc(sizeof(GLfloat)* 3 * (model->numvertices + 1));

	if (model->numtexcoords) {
		model->texcoords = (GLfloat*)malloc(sizeof(GLfloat)* 2 * (model->numtexcoords + 1));
	}
	if (model->numnormals) {
		model->normals = (GLfloat*)malloc(sizeof(GLfloat)* 3 * (model->numnormals + 1));
	}

	glmSecondPass(model, buf, size);
	glmThirdPass(model);
	return model;
}

/* glmDelete: Deletes a GLMmodel structure.
*
* model - initialized GLMmodel structure
*/
GLvoid
glmDelete(GLMmodel* model)
{
	GLMgroup* group;
	GLuint i;
	if(model == NULL)
	return;
	if (model->pathname)     free(model->pathname);
	if (model->mtllibname) free(model->mtllibname);
	if (model->vertices)     free(model->vertices);
	if (model->normals)  free(model->normals);
	if (model->texcoords)  free(model->texcoords);
	/*
		if (model->materials) {
		for (i = 0; i < model->nummaterials; i++)
		{
		free(model->materials[i].name);
		}
		free(model->materials);
		}
		*/
	while (model->groups) {
		group = model->groups;
		model->groups = model->groups->next;
		free(group->name);
		free(group->index_vertices);
		free(group->index_texcoords);
		free(group->index_normals);
		free(group);
	}

	free(model);
}
