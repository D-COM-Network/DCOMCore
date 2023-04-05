/*
Copyright [2022] [Cardiff University]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.dcom.core.compliancedocument.utils;

import java.util.UUID;


class GuidCompressor
{

	static char[] cConversionTable = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '_', '$' };

	public static String getNewIfcGloballyUniqueId() {
		Guid guid = getGuidFromUncompressedString(UUID.randomUUID().toString());
		String shortString = getCompressedStringFromGuid(guid);
		return shortString;
	}


	private static Guid getGuidFromUncompressedString(
			String uncompressedGuidString)
	{
		String[] parts = uncompressedGuidString.split("-");
		Guid guid = new Guid();
		guid.Data1 = Long.parseLong(parts[0], 16);
		guid.Data2 = Integer.parseInt(parts[1], 16);
		guid.Data3 = Integer.parseInt(parts[2], 16);

		String temp;

		temp = parts[3];
		guid.Data4[0] = (char) Integer.parseInt(temp.substring(0, 2), 16);
		guid.Data4[1] = (char) Integer.parseInt(temp.substring(2, 4), 16);

		temp = parts[4];
		guid.Data4[2] = (char) Integer.parseInt(temp.substring(0, 2), 16);
		guid.Data4[3] = (char) Integer.parseInt(temp.substring(2, 4), 16);
		guid.Data4[4] = (char) Integer.parseInt(temp.substring(4, 6), 16);
		guid.Data4[5] = (char) Integer.parseInt(temp.substring(6, 8), 16);
		guid.Data4[6] = (char) Integer.parseInt(temp.substring(8, 10), 16);
		guid.Data4[7] = (char) Integer.parseInt(temp.substring(10, 12), 16);

		return guid;
	}


	private static String getCompressedStringFromGuid(Guid guid) {
		long[] num = new long[6];
		char[][] str = new char[6][5];
		int i, j, n;
		String result = new String();

		//
		// Creation of six 32 Bit integers from the components of the GUID
		// structure
		//
		num[0] = (long) (guid.Data1 / 16777216); // 16. byte (pGuid->Data1 /
		// 16777216) is the same as
		// (pGuid->Data1 >>
		// 24)
		num[1] = (long) (guid.Data1 % 16777216); // 15-13. bytes (pGuid->Data1 %
		// 16777216) is the same as
		// (pGuid->Data1
		// & 0xFFFFFF)
		num[2] = (long) (guid.Data2 * 256 + guid.Data3 / 256); // 12-10. bytes
		num[3] = (long) ((guid.Data3 % 256) * 65536 + guid.Data4[0] * 256 + guid.Data4[1]); // 09-07.
		// bytes
		num[4] = (long) (guid.Data4[2] * 65536 + guid.Data4[3] * 256 + guid.Data4[4]); // 06-04.
		// bytes
		num[5] = (long) (guid.Data4[5] * 65536 + guid.Data4[6] * 256 + guid.Data4[7]); // 03-01.
		// bytes
		//
		// Conversion of the numbers into a system using a base of 64
		//
		n = 3;
		for (i = 0; i < 6; i++) {
			if (!cv_to_64(num[i], str[i], n)) {
				return null;
			}
			for (j = 0; j < str[i].length; j++)
				if (str[i][j] != '\0') result += str[i][j];

			n = 5;
		}
		return result;
	}

	private static boolean cv_to_64(long number, char[] code, int len) {
		long act;
		int iDigit, nDigits;
		char[] result = new char[5];

		if (len > 5) return false;

		act = number;
		nDigits = len - 1;

		for (iDigit = 0; iDigit < nDigits; iDigit++) {
			result[nDigits - iDigit - 1] = cConversionTable[(int) (act % 64)];
			act /= 64;
		}
		result[len - 1] = '\0';

		if (act != 0) return false;

		for (int i = 0; i < result.length; i++)
			code[i] = result[i];

		return true;
	}
}
