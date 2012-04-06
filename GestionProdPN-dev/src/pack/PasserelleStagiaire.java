package pack;
import java.io.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import javax.swing.*;

import data.StgMail;

import jxl.*;
import jxl.read.biff.*;

import jxl.write.*;
import jxl.write.biff.RowsExceededException;

/**
 * 
 * @author Jean-S�bastien BERON
 *
 */
public class PasserelleStagiaire {
	
	private static boolean good;
	private static final String pathFilePnc= "dataImport\\OATVPNC.xls";
	private static final String pathFilePnt= "dataImport\\OATVPNT.xls";
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static Date dateDemain;
	
	public static void creerListePourSms(){
		
		good = true;
		ArrayList<Stagiaire> StagiaireList = chargerTousStagiairesPNC();
		StagiaireList = FiltreBOContact(StagiaireList);
		ecritureListeSMSxls(StagiaireList);
		if (good) {
			JOptionPane.showMessageDialog(null, "<html>Operation termin�e !" +
					"<br>le fichier est dans dataExport\\ListeSMS.xls</html>", "Termine", JOptionPane.INFORMATION_MESSAGE);
		}// finsi
		
	}//fin creerListePourSms()
	
	public static void creerListePourTests() {
		good = true;
		ArrayList<Stagiaire> StagiaireList = chargerTousStagiairesPNC();
		StagiaireList = FiltreBOContact(StagiaireList);
		ecritureListeTests(StagiaireList);
		if (good) {
			JOptionPane.showMessageDialog(null, "<html>Operation termin�e !" +
					"<br>les fichiers sont dans dataExport/Tests...</html>", "Termine", JOptionPane.INFORMATION_MESSAGE);
		}//finsi
	}

	public static void creerListePourInterview() {
		good = true;
		String pPNCS_1 = "dataImport/PNC_S-1.xls";
		String pPNTS_1 = "dataImport/PNT_S-1.xls";
		
		// PNC
		try {
			ArrayList<StgMail> StgList = new ArrayList<StgMail>();
			File f = new File(pPNCS_1);
			Workbook wb = Workbook.getWorkbook(f);
			Sheet sh = wb.getSheet(0);
			StgMail h = new StgMail(sh.getRow(0),0);
			String[] qListes = { "CRM", "EAO", "EPU", "SEC", "SS", "SUR", "VOL" };
			PrintWriter[] P = { null, null, null, null, null, null, null };

			for (int i=1; i< sh.getRows(); i++) {
				Cell[] c = sh.getRow(i);
				StgList.add(new StgMail(c,i-1));
			}
			wb.close();

			for (int i=0; i< qListes.length; i++) {
				P[i] = new PrintWriter(new FileWriter("dataExport/Liste PNC-"+qListes[i]+".txt"));
				P[i].println(h.toString());
			}
			for (StgMail s : StgList) {
				P[s.n % qListes.length].println(s.toString());
			}
			for (int i=0; i< qListes.length; i++) {
				P[i].close();
			}
			
		} catch (BiffException e) {
			good = false;
			JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
					"<br>"+pPNCS_1+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			good = false;
			JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
					"<br>"+pPNCS_1+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
		
		
		// PNT
		try {
			ArrayList<StgMail> StgList = new ArrayList<StgMail>();
			File f = new File(pPNTS_1);
			Workbook wb = Workbook.getWorkbook(f);
			Sheet sh = wb.getSheet(0);
			StgMail h = new StgMail(sh.getRow(0),0);
			PrintWriter P;

			for (int i=1; i< sh.getRows(); i++) {
				Cell[] c = sh.getRow(i);
				StgList.add(new StgMail(c,i-1));
			}
			wb.close();

			P = new PrintWriter(new FileWriter("dataExport/Liste PNT-VOL.txt"));
			P.println(h.toString());

			for (StgMail s : StgList) {
				P.println(s.toString());
			}
			P.close();
			
		} catch (BiffException e) {
			good = false;
			JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
					"<br>"+pPNTS_1+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			good = false;
			JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
					"<br>"+pPNTS_1+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
		}

		if (good) {
			JOptionPane.showMessageDialog(null, "<html>Operation termin�e !" +
					"<br>les fichiers sont dans dataExport/Interview/...</html>", "Termine", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static ArrayList<Stagiaire> chargerTousStagiairesPNC(){
		
		ArrayList<Stagiaire> StagiaireList = new ArrayList<Stagiaire>();
			try {
				File fichier = new File(pathFilePnc);
				Workbook workbook;
				workbook = Workbook.getWorkbook(fichier);
				Sheet sheet = workbook.getSheet(0);
				for (int i = 1; i < sheet.getRows()-1; i++) {
					Cell[] cell = sheet.getRow(i);
					StagiaireList.add(new Stagiaire(cell[4].getContents(), cell[11].getContents(), cell[5].getContents()
							, cell[6].getContents(), cell[2].getContents(), cell[3].getContents()
							, cell[0].getContents(), cell[12].getContents()));
				}
			} catch (BiffException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
						"<br/>"+pathFilePnc+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
						"<br/>"+pathFilePnc+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			}

		return StagiaireList;
		
	}//fin chargerTousStagiairesPNC()
	
	public static ArrayList<Stagiaire> chargerTousStagiairesPNT(){
		
		ArrayList<Stagiaire> StagiaireList = new ArrayList<Stagiaire>();
			try {
				File fichier = new File(pathFilePnt);
				Workbook workbook;
				workbook = Workbook.getWorkbook(fichier);
				Sheet sheet = workbook.getSheet(0);
				for (int i = 1; i < sheet.getRows()-1; i++) {
					Cell[] cell = sheet.getRow(i);
					StagiaireList.add(new Stagiaire(cell[3].getContents(), cell[9].getContents(), cell[5].getContents()
							, cell[6].getContents(), cell[1].getContents(), cell[2].getContents()
							, cell[0].getContents(), cell[12].getContents()));
				}
			} catch (BiffException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
						"<br/>"+pathFilePnt+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
						"<br/>"+pathFilePnt+"</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			}

		return StagiaireList;
		
	}//fin chargerTousStagiairesPNT()
	
	public static ArrayList<Stage> ajoutPnt(ArrayList<Stage> stageList,ArrayList<Stagiaire> pntList){
		ArrayList<Stage> newStageList = stageList;
		ArrayList<String> stagePNTList = new ArrayList<String>();
		stagePNTList.add("S2");
		stagePNTList.add("QT");
		int index;
		for (Stage stage : newStageList) {
			index = -1;
			for (int i = 0 ; i < stagePNTList.size(); i++) {
				if(stage.getCodeI().startsWith(stagePNTList.get(i))){
					index = i;
					break;
				}
			}
			switch (index) {
			case 0:
				int n = 0;
				long modulo = 0;
				for (Stagiaire stagiaire : pntList) {
					if (stagiaire.getCodeStage().trim().endsWith("S2CC")) {
						// modulo pour les S2
						modulo = (n % stage.getIdxMax()) +1 ;
						if (modulo == stage.getIdx()) {
							System.out.println("Ajout PNT "+n+ " " + stagiaire.getNom() + " au stage " +stage.getCode());
							stage.ajoutStagiaire(stagiaire);
						}
						n++;
					}
				}
				break;
			case 1:
				ArrayList<String> qtinfo = new ArrayList<String>();
				String codeStage = stage.getCodeI();
				String chaine = "";
				for (int i = 0; i < codeStage.length(); i++) {
					if(codeStage.substring(i, i+1).equalsIgnoreCase(" ")){
						qtinfo.add(chaine);
						chaine = "";
					}else{
						chaine = chaine + codeStage.substring(i, i+1);
					}
				}
				qtinfo.add(chaine);
				for (Stagiaire stagiaire : pntList) {
					for (String string : qtinfo) {
						if (stagiaire.getCodeStage().startsWith(string)) {
							stage.ajoutStagiaire(stagiaire);
						}
					}
				}
				break;
			default:
				break;
			}
		}
		
		return newStageList;
	}//
	
	public static ArrayList<Stage> ajoutPnc(ArrayList<Stage> stageList,ArrayList<Stagiaire> stagiairePNCList) {
		ArrayList<Stage> newStageList = stageList;
		
		for (Stage stage : newStageList) {
			String strCodeStage = stage.getCodeI().replace(" ", "");
			for (Stagiaire stagiaire : stagiairePNCList) {
				String strCodeStagiaire = stagiaire.getCodeStage().replace(" ", "").trim();
				if(strCodeStagiaire.startsWith(strCodeStage)
						&& ! stage.getDateDt().before(stagiaire.getDateDeb())  
						&& ! stage.getDateDt().after(stagiaire.getDateFin())){
					stage.ajoutStagiaire(stagiaire);
				}else{
					if(strCodeStage.startsWith(strCodeStagiaire)
							&& ! stage.getDateDt().before(stagiaire.getDateDeb())  
							&& ! stage.getDateDt().after(stagiaire.getDateFin())){
						stage.ajoutStagiaire(stagiaire);
					}
				}
			}
		}
		
		return newStageList;
	}

	
	
	@SuppressWarnings("deprecation")
	private static ArrayList<Stagiaire> FiltreBOContact(ArrayList<Stagiaire> StagiaireList){
		
		ArrayList<Stagiaire> newStagiaireList = StagiaireList;
		
		ArrayList<String> stageList = new ArrayList<String>();
		ArrayList<Stagiaire> stagiaireGood = new ArrayList<Stagiaire>();
		String selDate = "";
		FileReader fichier;
			try {
				String ligne;
				fichier = new FileReader("dataSystem\\StageSMSPNC.txt");
				BufferedReader reader = new BufferedReader(fichier);
				while ((ligne = reader.readLine()) != null){
					stageList.add(ligne);
				}

				
				//recuperation de la date de demain
				Calendar cl=new GregorianCalendar();
				Date dateactuelle = new Date();
				if(dateactuelle.getDay() == 5){
					cl.add(Calendar.DATE, 3);
				}else{
					cl.add(Calendar.DATE, 1);
				}

				dateDemain = new Date((cl.get(Calendar.YEAR)-1900), cl.get(Calendar.MONTH), cl.get(Calendar.DATE));		
				selDate = JOptionPane.showInputDialog("S�l�ction de la date: ", dateFormat.format(dateDemain));
				dateDemain = dateFormat.parse(selDate);				

				for (Stagiaire stagiaire : newStagiaireList) {
					if(dateDemain.before(stagiaire.getDateDeb()) == false && dateDemain.after(stagiaire.getDateFin()) == false){
						for (String string : stageList) {
							if(stagiaire.getCodeStage().startsWith(string)){
								stagiaireGood.add(stagiaire);
								break;
							}
						}
					}
				}
				
			} catch (FileNotFoundException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>fichier non trouv�" +
						"<br/>dataSystem\\StageSMSPNC.txt</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme de lecture de" +
						"<br/>dataSystem\\StageSMSPNC.txt</html>", "Erreur", JOptionPane.ERROR_MESSAGE);;
			} catch (ParseException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme de format" +
						"<br/>de la date " + selDate + "</html>", "Erreur", JOptionPane.ERROR_MESSAGE);;
			}
			
		newStagiaireList.retainAll(stagiaireGood);
		
		return newStagiaireList;
		
	}//fin FiltreBOContact()
	
	
	private static void ecritureListeSMSxls(ArrayList<Stagiaire> StagiaireList){
		
			try {
				File file = new File("dataExport\\listeSMS.xls");
				WritableWorkbook workbook;
				workbook = Workbook.createWorkbook(file);
				WritableSheet sheet = workbook.createSheet("ListeMAT", 0);
				for (int i = 0; i < StagiaireList.size(); i++) {
					sheet.addCell(new Label(0, i, StagiaireList.get(i).getMatricule()));
				}
				workbook.write(); 
				workbook.close();
			} catch (IOException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme d'ecriture de" +
						"<br/>dataExport\\listeSMS.xls", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (RowsExceededException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme d'ecriture de" +
						"<br/>dataExport\\listeSMS.xls</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (WriteException e) {
				good = false;
				JOptionPane.showMessageDialog(null, "<html>probleme d'ecriture de" +
						"<br/>dataExport\\listeSMS.xls</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
			}

	}//fin ecritureListeSMSxls

	private static void ecritureListeTests(ArrayList<Stagiaire> stagiaireList) {
		Hashtable<String,StringBuffer> stgMap = new Hashtable<String,StringBuffer>();
		String pathDossier = "dataExport/Tests du "+dateFormat.format(dateDemain)+"/";
		String key = "";
		
		for (Stagiaire s : stagiaireList) {
			String code = s.getCodeStage().replace(" ", "").trim();
			code = code.substring(0,3)+" "+code.substring(3);
			String matr = "M"+s.getMatricule().subSequence(0, 6);
			if (stgMap.containsKey(code)) {
				//System.out.println("[INFO] ajout stage:"+code+" et matr:"+matr);
				stgMap.get(code).append("|"+matr);
			}
			else {
				//System.out.println("[INFO]   +++ stage:"+code+" et matr:"+matr);
				stgMap.put(code,new StringBuffer(matr));
			}
		}

		try {
			new File(pathDossier).mkdir();
	
			Enumeration<String> e = stgMap.keys();
			 while (e.hasMoreElements()) {
				  key = e.nextElement();
				  StringBuffer value = stgMap.get(key);
				  
				  FileWriter fichier = new FileWriter(pathDossier+key+".txt");
				  PrintWriter printer = new PrintWriter(fichier);
				  printer.println(value);
				  printer.close();
			 }
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "<html>probleme d'ecriture de" +
					"<br>"+pathDossier+key+".txt", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}



}//fin class
