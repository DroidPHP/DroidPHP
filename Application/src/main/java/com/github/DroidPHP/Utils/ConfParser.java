/**
 * This file is part of DroidPHP
 *
 * (c) 2013 Shushant Kumar
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.github.DroidPHP.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfParser {

	/**
	 * @param fileName
	 */
	public HashMap<String, String> Parser(String fileName) {

		String s = "";
		try {
			s = org.apache.commons.io.FileUtils.readFileToString(new File(
					fileName), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] mStrArr = s.split("\n");
		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < mStrArr.length; ++i) {
			/**
			 * If String does not contain "var." just skip it
			 */
			if (false == mStrArr[i].contains("var."))
				continue;
			/**
			 * Now we have found what we are looking for just spit it from = and
			 * convert it in to key and value pair
			 */

			String[] NodeList = mStrArr[i].split("=", 2);// OffSet is 2
			map.put(NodeList[0].trim().replace("var.", ""),
					filterValue(NodeList[1]).substring(1).trim());

		}
		return map;

	}

	public static String filterValue(String mString) {
		StringBuffer sb = new StringBuffer(mString);

		if (mString.startsWith("\"") || mString.startsWith("'")) {
			sb.setCharAt(0, ' ');

		}
		if (mString.endsWith("\"") || mString.endsWith("'")) {
			sb.deleteCharAt(mString.length() - 1);

		}
		mString = null;

		return sb.toString().trim();

	}
}