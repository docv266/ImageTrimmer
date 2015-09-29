package main;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Lanceur
{

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
		try
		{
			Document docRoot = Jsoup.connect(args[0]).get();

			File dossierGeneral = new File("sortie");

			// Créer le dossier s'il n'existe pas
			if (!dossierGeneral.exists())
			{
				dossierGeneral.mkdirs();
			}

			// http://annonce-moto.vivastreet.com/moto+gradignan-33170/600-bandit/124901707
			File dossierAnnonce = new File(dossierGeneral + File.separator + args[0].split("/")[5]);

			if (dossierAnnonce.exists())
			{

				FileUtils.deleteDirectory(dossierAnnonce);

			}

			dossierAnnonce.mkdirs();

			File sortie = new File(
					dossierGeneral + File.separator + dossierAnnonce.getName() + File.separator + "sortie.txt");

			BufferedWriter writer = new BufferedWriter(new FileWriter(sortie));

			writer.write(docRoot.select("h1").text().trim());
			writer.newLine();

			writer.write(docRoot.select(".shortdescription").text().trim());
			writer.newLine();

			writer.write(docRoot.select("td:contains(Année) + td").text().trim());
			writer.newLine();

			writer.write(docRoot.select("td:contains(Kilométrage) + td").text().trim());
			writer.newLine();

			writer.write(docRoot.select(".user_link").text().trim());
			writer.newLine();

			writer.write(docRoot.select(".vs-phone-button").attr("data-phone-number").trim());
			writer.newLine();

			writer.write(docRoot.select("td:contains(Ville/Code postal) + td").text().trim());
			writer.close();

			// Photos

			TraitementImage.saveImage(docRoot.select("#vs_photos_0 > img").attr("src").trim(),
					dossierGeneral + File.separator + dossierAnnonce.getName() + File.separator + "_1.jpg");

			Elements ele = docRoot.select("[id^=vs_photos_] > span");

			int i = 2;
			for (Element photo : ele)
			{
				TraitementImage.saveImage(photo.attr("src").trim(),
						dossierGeneral + File.separator + dossierAnnonce.getName() + File.separator + "_" + i + ".jpg");
				i++;
			}

			for (final File f : dossierAnnonce.listFiles(IMAGE_FILTER))
			{
				BufferedImage img = null;

				img = ImageIO.read(f);

				img = TraitementImage.trim(img, 0.2);

				img = TraitementImage.cropImage(img);

				File outputfile = new File(dossierAnnonce + "/" + f.getName());
				ImageIO.write(img, "jpg", outputfile);

			}

		}
		catch (

		IOException e1)

		{
			e1.printStackTrace();
			System.exit(0);
		}

	}

}
