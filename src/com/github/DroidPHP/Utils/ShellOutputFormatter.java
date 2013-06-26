/**
 * This file is part of DroidPHP
 *
 * (c) 2013 Shushant Kumar
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.github.DroidPHP.Utils;

public class ShellOutputFormatter {

	public static String toHTML(String mString) {

		/**
		 * Replace all repeating whitespace with one single whilespace
		 */

		mString = mString.replaceAll("\\s+", " ");
		// String[] mArrStr = mString.split("\n");
		// String s = "";
		// for (int i = 0; i < mArrStr.length; i++) {
		mString = "<strong>" + mString.trim() + "</strong><br/>";

		// }
		return mString.trim();

	}

}
