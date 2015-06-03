/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package self.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.RationalNumber;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

public class MetadataReader
{
	public  Date  getCreateDateAsFormatedString(File file) throws IOException,ParseException
	{
		Date exifCreateDate = null;
		//        get all metadata stored in EXIF format (ie. from JPEG or TIFF).
		//            org.w3c.dom.Node node = Sanselan.getMetadataObsolete(imageBytes);
		try 
		{
		IImageMetadata metadata = Sanselan.getMetadata(file);

		//System.out.println(metadata);

		if (metadata instanceof JpegImageMetadata)
		{
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;



			System.out.println("file: " + file.getPath());

			// get DateField
			String exifCreateDateAndTime = null;
			TiffField exifCreateDateAndTimeRef = jpegMetadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE);
			if( exifCreateDateAndTimeRef != null)
			{
			
			// Convert String in Date Object
			DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			Date oldDate = formatter.parse(exifCreateDateAndTimeRef.getStringValue());
			exifCreateDate = oldDate;
			}

		}
		}
		catch(ImageReadException ire)
		{
			ire.printStackTrace();
			throw new IOException (ire.getMessage());
		}

		return exifCreateDate;
	}

	private  void printTagValue(JpegImageMetadata jpegMetadata,
			TagInfo tagInfo) throws ImageReadException, IOException
	{
		TiffField field = jpegMetadata.findEXIFValue(tagInfo);
		if (field == null)
			System.out.println(tagInfo.name + ": " + "Not Found.");
		else
			System.out.println(tagInfo.name + ": "
					+ field.getValueDescription());
	}

}
