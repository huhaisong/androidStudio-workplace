#include <math.h>
#include <string.h>
#include "So3Util.h"

const double M_SQRT12  = 0.7071067811865476;
const double ONE_6TH   = 0.1666666716337204;
const double ONE_20TH  = 0.1666666716337204;


void init_So3Util(pSo3Util des)
{
	memset(des, 0, sizeof(So3Util));
}

void sO3FromTwoVec(pSo3Util des, pVector3d a, pVector3d b, pMatrix3x3d result)
{
	cross_Vector3d(a, b, &des->sO3FromTwoVecN);
	
	if (length_Vector3d(&des->sO3FromTwoVecN) == 0)
	{
		double r11 = dot_Vector3d(a, b);
		if (r11 >= 0.0)
		{
			setIdentity_Matrix3x3d(result);
		}
		else
		{
			ortho_Vector3d(a, &des->sO3FromTwoVecRotationAxis);
			rotationPiAboutAxis(des, &des->sO3FromTwoVecRotationAxis, result);
		}
	}
	else
	{
		copy_Vector3d(&des->sO3FromTwoVecA, a);
		copy_Vector3d(&des->sO3FromTwoVecB, b);

		normalize_Vector3d(&des->sO3FromTwoVecN);
		normalize_Vector3d(&des->sO3FromTwoVecA);
		normalize_Vector3d(&des->sO3FromTwoVecB);

		pMatrix3x3d r1 = des->sO3FromTwoVec33R1;
		setColumn_Matrix3x3d(r1, 0, &des->sO3FromTwoVecA);
		setColumn_Matrix3x3d(r1, 1, &des->sO3FromTwoVecN);
		cross_Vector3d(&des->sO3FromTwoVecN, &des->sO3FromTwoVecA, &des->temp31);
		setColumn_Matrix3x3d(r1, 2, &des->temp31);

		pMatrix3x3d r2  = des->sO3FromTwoVec33R2;
		setColumn_Matrix3x3d(r2, 0, &des->sO3FromTwoVecB);
		setColumn_Matrix3x3d(r2, 1, &des->sO3FromTwoVecN);
		cross_Vector3d(&des->sO3FromTwoVecN, &des->sO3FromTwoVecB, &des->temp31);
		setColumn_Matrix3x3d(r2, 2, &des->temp31);
		transpose_Matrix3x3d(r1);
		mult_Matrix3x3d(r2, r1, result);
	}
}

void rotationPiAboutAxis(pSo3Util des, pVector3d v, pMatrix3x3d result)
{
	double invTheta = 0.3183098861837907;
	double kA = 0.0;
	double kB = 0.20264236728467558;
	copy_Vector3d(&des->rotationPiAboutAxisTemp, v);
	scale_Vector3d(&des->rotationPiAboutAxisTemp, 3.141592653589793 / length_Vector3d(&des->rotationPiAboutAxisTemp));
	rodriguesSo3Exp(&des->rotationPiAboutAxisTemp, kA, kB, result);
}

void sO3FromMu(pVector3d w, pMatrix3x3d result)
{
	double thetaSq = dot_Vector3d(w, w);
	double theta = sqrt(thetaSq);
	double kA,kB;

	if (thetaSq < 1.0e-8)
	{
		kA = 1.0 - 0.1666666716337204 * thetaSq;
		kB = 0.5;
	}
	else if (thetaSq < 1.0E-6)
	{
		kB = 0.5 - 0.0416666679084301 * thetaSq;
		kA = 1.0 - thetaSq * 0.1666666716337204 * (1.0 - 0.1666666716337204 * thetaSq);
	}
	else
	{
		double invTheta = 1.0 / theta;
		kA = sin(theta) * invTheta;
		kB = (1.0 - cos(theta)) * (invTheta * invTheta);
	}
	rodriguesSo3Exp(w, kA, kB, result);
}

void muFromSO3(pSo3Util des, pMatrix3x3d so3, pVector3d result)
{
	double cosAngle = (get_element_Matrix3x3d(so3, 0, 0) + get_element_Matrix3x3d(so3, 1, 1) + get_element_Matrix3x3d(so3, 2, 2)-1)*0.5;
	set_Vector3d(result,
		(get_element_Matrix3x3d(so3, 2, 1) - get_element_Matrix3x3d(so3, 1, 2)) / 2,
		(get_element_Matrix3x3d(so3, 0, 2) + get_element_Matrix3x3d(so3, 2, 0)) / 2,
		(get_element_Matrix3x3d(so3, 1, 0) + get_element_Matrix3x3d(so3, 0, 1)) / 2);

	double sinAngleAbs = length_Vector3d(result);
	
	if (cosAngle > 0.7071067811865476)
	{
		if (sinAngleAbs > 0.0) 
		{
			scale_Vector3d(result, asin(sinAngleAbs) / sinAngleAbs);
		}
	}
	else
	{
		double angle;
		if (cosAngle > -0.7071067811865476)
		{
			angle = acos(cosAngle);
			scale_Vector3d(result, angle / sinAngleAbs);
		}
		else
		{
			double d0, d1, d2;
			angle = 3.141592653589793 - asin(sinAngleAbs);
			d0 = get_element_Matrix3x3d(so3, 0, 0) - cosAngle;
			d1 = get_element_Matrix3x3d(so3, 1, 1) - cosAngle;
			d2 = get_element_Matrix3x3d(so3, 2, 2) - cosAngle;

			pVector3d r2 = &des->muFromSO3R2;
			if ((d0 * d0 > d1 * d1) && (d0 * d0 > d2 * d2)) 
			{
				set_Vector3d(r2, d0, 
					(get_element_Matrix3x3d(so3, 1, 0) + get_element_Matrix3x3d(so3, 0, 1)) / 2.0,
					(get_element_Matrix3x3d(so3, 0, 2) + get_element_Matrix3x3d(so3, 2, 0)) / 2.0);
			}
			else if (d1 * d1 > d2 * d2) 
			{
				set_Vector3d(r2, 
					(get_element_Matrix3x3d(so3, 1, 0) + get_element_Matrix3x3d(so3, 0, 1)) / 2.0,
					d1,
					(get_element_Matrix3x3d(so3, 2, 1) + get_element_Matrix3x3d(so3, 1, 2)) / 2.0);
			}
			else 
			{
				set_Vector3d(r2, 
					(get_element_Matrix3x3d(so3, 0, 2) + get_element_Matrix3x3d(so3, 2, 0)) / 2.0,
					(get_element_Matrix3x3d(so3, 2, 1) + get_element_Matrix3x3d(so3, 1, 2)) / 2.0, 
					d2);
			}

			if (dot_Vector3d(r2, result) < 0.0) {
				scale_Vector3d(r2, -1.0);
			}
			normalize_Vector3d(r2);
			scale_Vector3d(r2, angle);
			copy_Vector3d(result, r2);
		}
	}
}

void rodriguesSo3Exp(pVector3d w, double kA, double kB, pMatrix3x3d result)
{
	double a = w->x * w->x;
	double b = w->y * w->y;
	double wz2 = w->z * w->z;


	set_element_Matrix3x3d(result, 0, 0, 1.0 - kB * (b + wz2));
	set_element_Matrix3x3d(result, 1, 1, 1.0 - kB * (a + wz2));
	set_element_Matrix3x3d(result, 2, 2, 1.0 - kB * (a + b));


	a = kA * w->z;
	b = kB * w->x * w->y;
	set_element_Matrix3x3d(result, 0, 1, b - a);
	set_element_Matrix3x3d(result, 1, 0, b + a);

	a = kA * w->y;
	b = kB * w->x * w->z;
	set_element_Matrix3x3d(result, 0, 2, b + a);
	set_element_Matrix3x3d(result, 2, 0, b - a);
	a = kA * w->x;
	b = kB * w->y * w->z;
	set_element_Matrix3x3d(result, 1, 2, b - a);
	set_element_Matrix3x3d(result, 2, 1, b + a);
}

void generatorField(int i, pMatrix3x3d pos, pMatrix3x3d result)
{
	double tmp;
	set_element_Matrix3x3d(result, i, 0, 0);

	tmp = get_element_Matrix3x3d(pos, (i + 2) % 3, 0);
	set_element_Matrix3x3d(result, (i + 1) % 3, 0, -tmp);

	tmp = get_element_Matrix3x3d(pos, (i + 1) % 3, 0);
	set_element_Matrix3x3d(result, (i + 2) % 3, 0, tmp);

}
