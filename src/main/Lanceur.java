package main;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

public class Lanceur
{

	// File representing the folder that you select using a FileChooser
	static final File dir = new File(".");

	// array of supported extensions (use a List if you prefer)
	static final String[] EXTENSIONS = new String[]
	{ "gif", "png", "bmp", "jpg" // and other formats you need
	};

	// filter to identify images based on their extensions
	static final FilenameFilter IMAGE_FILTER = new FilenameFilter()
	{

		@Override
		public boolean accept(final File dir, final String name)
		{
			for (final String ext : EXTENSIONS)
			{
				if (name.endsWith("." + ext))
				{
					return (true);
				}
			}
			return (false);
		}
	};

	public static void main(String[] args)
	{
		File dossier = new File("sortie");

		// Créer le dossier s'il n'existe pas
		if (dossier.exists())
		{
			try
			{
				FileUtils.deleteDirectory(dossier);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		dossier.mkdirs();

		if (dir.isDirectory())
		{ // make sure it's a directory
			for (final File f : dir.listFiles(IMAGE_FILTER))
			{
				BufferedImage img = null;

				try
				{
					img = ImageIO.read(f);

					img = trim(img);
					img = cropImage(img);

					File outputfile = new File(dossier + "/" + f.getName());
					ImageIO.write(img, "jpg", outputfile);
				}
				catch (final IOException e)
				{
					// handle errors here
				}
			}
		}

	}

	static private BufferedImage cropImage(BufferedImage src)
	{
		BufferedImage dest = src.getSubimage(0, 0, src.getWidth(), src.getHeight() - 60);
		return dest;
	}

	static BufferedImage trim(BufferedImage image)
	{
		int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = 0, y2 = 0;
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				int argb = image.getRGB(x, y);
				if (argb != -1)
				{
					x1 = Math.min(x1, x);
					y1 = Math.min(y1, y);
					x2 = Math.max(x2, x);
					y2 = Math.max(y2, y);
				}
			}
		}
		WritableRaster r = image.getRaster();
		ColorModel cm = image.getColorModel();
		r = r.createWritableChild(x1, y1, x2 - x1, y2 - y1, 0, 0, null);
		return new BufferedImage(cm, r, cm.isAlphaPremultiplied(), null);
	}

}
