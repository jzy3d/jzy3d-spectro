/*
 * Spectro-Edit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectro-Edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package net.bluecow.spectro;

import java.text.DecimalFormat;

/**
 * Static container of utility methods for working with FFT data and operations.
 */
class FFT {

    /**
     * Just a container for static methods.  Do not create instances.
     */
    private FFT() {
        // empty placeholder
    }

    /**
     * FFT routine. The array length must be a power of two. The array
     * size is [L][2], where each sample is complex; array[n][0] is
     * the real part, array[n][1] is the imaginary part of sample n.
     *
     * @author Jeffrey D. Taft, PhD. (see http://www.nauticom.net/www/jdtaft/JavaFFT.htm)
     */
    public static double[][] fft_1d( double[][] array ) {
	double  u_r,u_i, w_r,w_i, t_r,t_i;
	int     ln, nv2, k, l, le, le1, j, ip, i, n;
	
	n = array.length;
    	ln = (int)( Math.log( (double)n )/Math.log(2) + 0.5 );
    	nv2 = n / 2;
    	j = 1;
 	for (i = 1; i < n; i++ ) {
	    if (i < j) {
		t_r = array[i - 1][0];
		t_i = array[i - 1][1];
		array[i - 1][0] = array[j - 1][0];
		array[i - 1][1] = array[j - 1][1];
		array[j - 1][0] = t_r;
		array[j - 1][1] = t_i;
	    }
	    k = nv2;
	    while (k < j) {
		j = j - k;
		k = k / 2;
	    }
	    j = j + k;
    	}
	
 	for (l = 1; l <= ln; l++) {
	    /* loops thru stages */
	    le = (int)(Math.exp( (double)l * Math.log(2) ) + 0.5 );
	    le1 = le / 2;
	    u_r = 1.0;
	    u_i = 0.0;
	    w_r =  Math.cos( Math.PI / (double)le1 );
	    w_i = -Math.sin( Math.PI / (double)le1 );
	    for (j = 1; j <= le1; j++) {
		/* loops thru 1/2 twiddle values per stage */
		for (i = j; i <= n; i += le) {
		    /* loops thru points per 1/2 twiddle */
		    ip = i + le1;
		    t_r = array[ip - 1][0] * u_r - u_i * array[ip - 1][1];
		    t_i = array[ip - 1][1] * u_r + u_i * array[ip - 1][0];
		    
		    array[ip - 1][0] = array[i - 1][0] - t_r;
		    array[ip - 1][1] = array[i - 1][1] - t_i; 
		    
		    array[i - 1][0] =  array[i - 1][0] + t_r;
		    array[i - 1][1] =  array[i - 1][1] + t_i;  
		}
		t_r = u_r * w_r - w_i * u_i;
		u_i = w_r * u_i + w_i * u_r;
		u_r = t_r;
	    }
    	}
	return array;
    } /* end of fft_1d */

    /**
     * Takes the command line arguments, converts them into a
     * floating-point array, then runs the FFT_1d transform on the
     * array twice.  The array is printed before and after each
     * transformation.
     */
    public static void main(String[] args) throws NumberFormatException {
	double[][] fftBuf = new double[args.length][2];
	for (int i = 0; i < args.length; i++) {
	    //fftBuf[i] = new double[2];
	    fftBuf[i][0] = Double.parseDouble(args[i]);
	    fftBuf[i][1] = 0.0;
	}

	printArray(fftBuf);
	fft_1d(fftBuf);
	printArray(fftBuf);
	fft_1d(fftBuf);
	scaleArray(fftBuf, 1.0 / (double) fftBuf.length);
	reverseArray(fftBuf);
	printArray(fftBuf);
    }

    public static void printArray(double[][] fftBuf) {
	DecimalFormat real = new DecimalFormat("0.000000000");
	real.setPositivePrefix(" ");
	DecimalFormat imag = new DecimalFormat("0.000000000");
	imag.setNegativePrefix(" - ");
	imag.setPositivePrefix(" + ");

	System.out.println("{");
	for (int i = 0; i < fftBuf.length; i++) {
	    System.out.print(" (");
	    System.out.print(real.format(fftBuf[i][0]));
	    System.out.print(imag.format(fftBuf[i][1]));
	    System.out.println("i)");
	}
	System.out.println("}");
    }

    public static void scaleArray(double[][] fftBuf, double scalar) {
	for (int i = 0; i < fftBuf.length; i++) {
	    fftBuf[i][0] *= scalar;
	    fftBuf[i][1] *= scalar;
	}
    }

    public static void reverseArray(double[][] fftBuf) {
	for (int i = 1, n = fftBuf.length; i < n/2; i++) {
	    double[] temp = fftBuf[i];
	    fftBuf[i] = fftBuf[n-i];
	    fftBuf[n-i] = temp;
	}
    }
}
