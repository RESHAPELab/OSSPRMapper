package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DAO.FileDAO;
import model.Apriori;
import model.AprioriNew;
import model.PrIssue;

public class MainMapper {
	private String user = "postgres";
	private String pswd = "admin";
	private String project = "jabref";
	private String db = "dev";
	private String file = "dat.txt";
	private String pr = null;
	private String java = null;
	private String csv = null;
	private int isOnlyCSV = 0;
	private String separator = ",";
	private String title = null;
	private String body = null;
	private String bin = null;
	private ArrayList<AprioriNew> apns = new ArrayList<AprioriNew>();
	private String classes = null;
	
	private String prRes = "";
	private String issue = "";
	private String issueTitle = "";
	private String issueBody = "";
	private String issueComments = ""; 
	private String issueTitleLink = "";
	private String issueBodyLink = "";
	private String issueCommentsLink = ""; 
	private int isPR = 0; 
	private int isTrain = 0; 
	private String commitMessage = "";
	private String prComments = ""; 


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainMapper mp = new MainMapper();
		mp.execute(args);
		
		
	}


	private void execute(String[] args) {
		// TODO Auto-generated method stub
		user 		= args[0];
		pswd 		= args[1];
		project 	= args[2];
		db 			= args[3];
		file 		= args[4];
		csv 		= args[5];
		isOnlyCSV 	= Integer.parseInt(args[6]);
		separator 	= args[7];
		bin 		= args[8];
		classes 	= args[9];
		
		if (isOnlyCSV==1)
		{
			getPrs(); // apriori body title
			genBinaryExit(); //binary body title
		}
		else 
		{
			readData();
		}
	}


	private void genBinaryExit() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream os = new FileOutputStream(bin);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
	    	//bw.write("header \n");
			String line = "";
			String beginning = "";
			FileDAO dao = FileDAO.getInstancia(db, user, pswd);
			// write header
			line = line + "pr";
			// all general classifications possible
			ArrayList<String> dbGenerals = dao.getDistinctGenerals();
			for (int k=0; k<dbGenerals.size(); k++) {
				line = line + ";"+dbGenerals.get(k);
			}
			line = line + ";Title;Body;prIssue;issue;issueTitle;issueBody;issueComments;issueTitleLink;issueBodyLink;issueCommentsLink;isPR;isTrain;commitMessage;Comments\n";
					
			bw.write(line);
			
			// end header
			boolean found = false;
			int pr = 0;
			// find classification for each PR
			for (int i=0; i<apns.size(); i++) {
				AprioriNew apnAux = apns.get(i);
				ArrayList<String> gs = apnAux.getGenerals();
				pr = apnAux.getPr();
							
				// order line in order of generals generals
				
				ArrayList<String> printLine = new ArrayList();			
				
				// Search for labels to after use zeros and ones
				for (int t=0; t<dbGenerals.size(); t++) {
					for (int j=0; j<gs.size(); j++) {
						if (gs.get(j).equals(dbGenerals.get(t))){
							found = true;
						}
						
					}
					if (found){
						printLine.add(t,"1" );
						found = false;
					}
					else {
						printLine.add(t, "0");
					}
				}
				
				line = "";
				line = line + pr;
				
				if(apnAux.getPr()==18)
				{
					System.out.println("Debug");
				}
				// add zeros and ones to line to print
				for (int j=0; j<printLine.size(); j++) {
					line = line + ";"+printLine.get(j);
				}
				beginning = line;
				// fill PT title and body
				ArrayList<String> result = dao.getTitleBody(pr);
				String title = result.get(0);
				String body = result.get(1);
				if(title.equals("nan")) {
					title="";
				}
				if(body.equals("nan")) {
					body="";
				}
				//line = line + ";"+title+ ";"+body;// title and body
				
				// get issues
				ArrayList<PrIssue> linkedIssues = dao.getIssues(pr);
				
				if (linkedIssues.size()==1) { 
					PrIssue pri = new PrIssue();
					pri = linkedIssues.get(0);
					 
					prRes = pri.getPr();
					 issue = pri.getIssue();
					 issueTitle = pri.getIssueTitle();
					 issueBody = pri.getIssueBody();
					 issueComments  = pri.getIssueComments();
					 issueTitleLink = pri.getIssueTitleLink();
					 issueBodyLink  = pri.getIssueBodyLink();
					 issueCommentsLink  = pri.getIssueCommentsLink();
					 isPR   = pri.getIsPR();
				 
					 isTrain = pri.getIsTrain();   
					 commitMessage  = pri.getCommitMessage();
					 prComments = pri.getPrComments();
					 line = line + ";"+title+ ";"+body;
					 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
					 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
					line = line + "\n";
					bw.write(line);
					line = "";

				} else { // to generate one pr line with all issues together
					// initialize to accumulate
					if (linkedIssues.size()==0) { 
						  prRes = "";
						  issue = "";
						  issueTitle = "";
						  issueBody = "";
						  issueComments = ""; 
						  issueTitleLink = "";
						  issueBodyLink = "";
						  issueCommentsLink = ""; 
						  isPR = 1; 
						  isTrain = 0; 
						  commitMessage = ""; 
						  prComments = ""; 
							 line = line + ";"+title+ ";"+body;
							 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
							 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
							line = line + "\n";
							bw.write(line);
							line = "";
	
					}
					else {
						  if (pr==452) {
							  System.out.println("debug");
						  }
						  
						 for (int t=0; t<linkedIssues.size(); t++) { //// to generate one pr line with all issues together
								PrIssue pri = new PrIssue();
								pri = linkedIssues.get(t);
		
								 prRes = pri.getPr(); // pr do not acc
								 
								 issue =  pri.getIssue();
								 issueTitle =  pri.getIssueTitle();// do not acc
								 issueBody = pri.getIssueBody();// do not acc
								 issueComments  = pri.getIssueComments();// do not acc
								 
								 issueTitleLink = pri.getIssueTitleLink();
								 issueBodyLink  =  pri.getIssueBodyLink();
								 issueCommentsLink  = pri.getIssueCommentsLink();
								 
								 isPR   = pri.getIsPR(); // do not acc
								 
								 isTrain = pri.getIsTrain();   // do not acc
								 
								 commitMessage  = pri.getCommitMessage(); // do not acc
								 prComments = pri.getPrComments(); // do not acc
								 line = beginning + ";"+title+ ";"+body;// title and body
								 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
								line = line + "\n";
								bw.write(line);
								line = "";

						 }
					}
				}
				// concatenate issue data in line
				//line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;

				//line = line + "\n";
				//bw.write(line);
				//line = "";
	    	}
	    	bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void readData() {
		InputStream is = null;

		try 
		{
			is = new FileInputStream(file);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String s = null;

	    // prime loop by gathering first string
		try 
		{
			s = br.readLine();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}// primeira linha do arquivo

		ArrayList<String> api = null;

		while (s != null)
		{
			System.out.println("\nLine: " + s);
			splitLine(s);
			api = findAPI(pr, java, project);

			if (api==null)
				System.out.println("not found in " + project + ": " + pr + " - " + java);
			else 
			{
				insertApriori(api);
				insertPr();
			}

			// try to 
			try 
			{
				s = br.readLine();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		generateFile();
		
		try 
		{
			br.close();
			isr.close();
			is.close();

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}


	private void generateFile() {
		// TODO Auto-generated method stub
		getPrs();
	}


	private void getPrs() {
		
		// get FileDAO obj
		FileDAO fd = FileDAO.getInstancia( db, user, pswd );

		// get PRs from database
		ArrayList<Apriori> aps = fd.getAprioris( project );
		
		// if no PRs found in PR array
		if (aps==null)
		{
			System.out.println("No apriori found!!!");
		}
		else 
		{
			int prAux 		= 0;
			int pr 		 	= 0;
			
			// create new Apriori obj
			AprioriNew apn  = new AprioriNew();
		
			// loop through array of PRs
			for( int i=0; i<aps.size(); i++ ) 
			{	
				// get PR object and PR num member variable (int) 
				Apriori ap = aps.get(i);
				pr = ap.getPr();
				
				// on first iteration, set new Apriori obj and prAux to 
				// pr info from above
				if( i==0 ) 
				{ 
					// first case treatment
					apn.setPr(pr);
					apn.insertGeneral(ap.getGeneral()); // using the general field to store expert 
					prAux = pr;
				}
				else 
				{
					// if pr num is same as during last iteration 
					if (pr==prAux) 
					{
						apn.insertGeneral(ap.getGeneral());
						if (i+1==aps.size()) 
						{ 
							// last case treatment
							apns.add(apn);
						}
					} 
					else 
					{
						apns.add(apn);
						
						prAux = pr;
						
						apn = new AprioriNew();
						apn.setPr(pr);
						apn.insertGeneral(ap.getGeneral());
					}
				}
				
			}
			
			// write output
			try 
			{
				FileOutputStream os = new FileOutputStream(csv);
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				
				FileOutputStream osc = new FileOutputStream(classes);
				OutputStreamWriter oswc = new OutputStreamWriter(osc);
				BufferedWriter bwc = new BufferedWriter(oswc);
		    	//bw.write("header \n");
				String line = "";
				String lineClasses = "";
				
				
				for (int i=0; i<apns.size(); i++) 
				{
					AprioriNew apnAux = apns.get(i);
					ArrayList<String> gs = apnAux.getGenerals();
					pr = apnAux.getPr();
					FileDAO dao = FileDAO.getInstancia(db, user, pswd);
					ArrayList<String> result = dao.getTitleBody(pr);
					String title = result.get(0);
					String body = result.get(1);
					if (title!=null&&!title.contentEquals("nan")&&!title.equals("NaN")&&!title.isEmpty()){
						if (body!=null&&!body.contentEquals("nan")&&!body.equals("NaN")&&!body.isEmpty()){
							
							title = filter_text(title);
							body = filter_text(body);
							line = line + pr;
							lineClasses = lineClasses + pr +";";
							//line = line + ","+result.get(0)+ ","+result.get(1);// title and body
							line = line + ","+title+ ","+body;// title and body
							
							if(apnAux.getPr()==18)
							{
								System.out.println("Debug");
							}
							
							for (int j=0; j<gs.size(); j++) 
							{
								line = line + ","+gs.get(j);
								if (j==(gs.size()-1))
									
									lineClasses = lineClasses + gs.get(j);
								else
									lineClasses = lineClasses + gs.get(j)+"-";
							}
							
							line = line + "\n";
							lineClasses = lineClasses + "\n";
							bw.write(line);
							bwc.write(lineClasses);
						}
					}
					
					line = "";
					lineClasses = "";
		    	}
		    	
				bw.close();
		    	bwc.close();
		    	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
			
	}

	private String removeUrl(String commentstr)
	{
		int tam = commentstr.length();
	    String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	    Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(commentstr);
	    StringBuffer sb = new StringBuffer(tam);
	    while (m.find()) {
	        m.appendReplacement(sb, "");
	    }
	    return sb.toString();
	}
	
	private String filter_text(String str) {
		// TODO Auto-generated method stub
		String newstr = str.toLowerCase();
		newstr = newstr.trim();
		newstr = newstr.replaceAll("http.*?\\s", " ");
		//newstr = removeUrl(newstr);
		
		ArrayList <String> yourList = new ArrayList();
		yourList.add("[WIP]");
		yourList.add("[wip]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		yourList.add("[ ]");
		yourList.add("[ x ]");
		yourList.add("[x]");
		yourList.add("[x ]");
		yourList.add("[ x]");
		yourList.add("[]");
		
		yourList.add("Tests created for changes (if applicable)");
		yourList.add("Tests created for changes");
		
		yourList.add("Screenshots added in PR description");
		yourList.add("screenshots added");
		
		yourList.add("Ensured that [the git commit message is a good one]");
		
		yourList.add("Check documentation status");	
		yourList.add("Checked documentation");
		
		yourList.add("tests green");

		yourList.add("changes in pull request outlined? (what  why  ...)"); 
		yourList.add("what why");

		yourList.add("Changes in pull request outlined");

		yourList.add("Commits squashed");	
		
		yourList.add("<!--  Describe the changes you have made here: what  why  ...  Link issues by using the following pattern: #");
		yourList.add("<!-- describe the changes you have made here: what  why");
		yourList.add("<!-- describe the changes you have made here:");
		yourList.add("what  why");
		yourList.add("...");       


		yourList.add("Link issues by using the following pattern: [#333]");
		yourList.add("link issues by using the following pattern:");
		yourList.add("[koppor#47](https://github.com/koppor/jabref/issues/47");
		
		yourList.add("or [koppor#49]");
		yourList.add("or [koppor#");
		yourList.add("[koppor#");
		yourList.add("https://github.com/JabRef/jabref/issues/333");
		yourList.add("https://github.com/koppor/jabref/issues/47");      
		yourList.add("https://github.com/jabref/jabref/issues/");
		yourList.add("https://github.com/koppor/jabref/issues/"); 
		yourList.add("https://github.com/jabref/jabref/pull/");
		yourList.add("https://github.com/jabref/jabref/pull/");

		yourList.add("[#");

		
		yourList.add("The title of the PR must not reference an issue  because GitHub does not support autolinking there. -->");
		yourList.add("The title of the PR must not reference an issue");  
		yourList.add("because GitHub does not support autolinking there. -->");

		
		yourList.add("If you fixed a koppor issue  link it with following pattern");
		yourList.add("If you fixed a koppor issue");
		yourList.add("link it with following pattern");

		yourList.add("fixes https://github.com/jabref/jabref/issues/");
		yourList.add("fixes https://github.com/koppor/jabref/issues/");
		yourList.add("Fixes #");
		yourList.add("Fixes  #");
		yourList.add("Fix #");
		yourList.add("fix issue");
		yourList.add("resolve #");
		yourList.add("resolves #");
		yourList.add("followup from #");
		yourList.add("localizationupd");
		yourList.add("githubusercont");

		yourList.add("![image](https://user-images.githubusercontent.com/");
		yourList.add("![image](https://user-images.githubusercontent.com/");
		yourList.add("![modification](https://user-images.githubusercontent.com/");
		yourList.add("![modification](https://user-images.githubusercontent.com/");
		yourList.add("![grafik](https://user-images.githubusercontent.com/");
		yourList.add("![grafik](https://user-images.githubusercontent.com/");
		yourList.add("![littlebefore](https://user-images.githubusercontent.com/");
		yourList.add("![preferences](https://user-images.githubusercontent.com/");
		yourList.add("![preferences](https://user-images.githubusercontent.com/");
		yourList.add("![image]");
		yourList.add("![modification]");
		yourList.add("![grafik]");
		yourList.add("![preferences](https://user-images.githubusercontent.com/");
		yourList.add("![preferences]");
		
		yourList.add("https://user-images.githubusercontent.com/");
				
		yourList.add("<!--  - All items with  [ ]  are still a TODO. - All items checked with  [x]  are done. - Remove items not applicable -->");
		yourList.add("<!--  - All items with  [ ]  are still a TODO.");
		yourList.add("<!--  - All items with");
		yourList.add("are still a TODO.");
		yourList.add("All items checked with");
		yourList.add("are done");
		yourList.add("Remove items not applicable -->");
		
		yourList.add("Change in CHANGELOG.md described (if applicable)");
		yourList.add("Change in CHANGELOG.md described");
		yourList.add("for bigger UI changes");


		
		yourList.add("Manually tested changed features in running JabRef (always required)");
		yourList.add("Manually tested changed features in running JabRef ");
		
		
		yourList.add("Is the information available and up to date?"); 
		
		yourList.add("If not: Issue created at"); 
		yourList.add("Issue created for outdated help page at");
		yourList.add("Internal SQ");
		yourList.add("If you changed the localization: Did you run  gradle localizationUpdate");
		yourList.add("Internal quality assurance");
		
		
		yourList.add("Replace copy and rename");
		
		yourList.add("expandFileName "); 
		yourList.add("shortenFileName"); 
		
		yourList.add("Aux File listener? - [ ] introduce new paper folder?"); 
		yourList.add("Look for all aux files in paper folder"); 
		yourList.add("create icon inside groups menu/groups sidepane or under tools");
		yourList.add("Introduce an EventBus object being passed around. This enables better testing");
		yourList.add("Make  DatabaseChangeEvent  abstract and add subclasses according to  DatbaseChangeEvent");  
		yourList.add("Rewrite the currently existing code to use that event bus instead of");  
		yourList.add("net.sf.jabref.model.database.BibDatabase.addDatabaseChangeListener(DatabaseChangeListener)");  
		yourList.add("net.sf.jabref.model.database.BibDatabase.removeDatabaseChangeListener(DatabaseChangeListener)");
		
		yourList.add("Mostly GUI changes  testing makes not that much sense here");
		yourList.add("Mostly GUI changes testing makes not that much sense here");
		
		

		 
		/*yourList.add("[x] Change in CHANGELOG.md described");
		yourList.add("[x] Tests created for changes");
		yourList.add("[x] Manually tested changed features in running JabRef ");
		yourList.add("[x] Screenshots added in PR description");		
		yourList.add("[x] Ensured that [the git commit message is a good one]");
		yourList.add("[x] Check documentation status");
		yourList.add("[x] tests green?");
		yourList.add("[x] commits squashed?");
		yourList.add("[x] changes in pull request outlined? (what  why  ...)"); 
		
		yourList.add("[ x ] Change in CHANGELOG.md described");
		yourList.add("[ x ] Tests created for changes");
		yourList.add("[ x ] Manually tested changed features in running JabRef ");
		yourList.add("[ x ] Screenshots added in PR description");		
		yourList.add("[ x ] Ensured that [the git commit message is a good one]");
		yourList.add("[ x ] Check documentation status");
		yourList.add("[ x ] tests green?");
		yourList.add("[ x ] commits squashed?");
		yourList.add("[ x ] changes in pull request outlined? (what  why  ...)"); */
		
		yourList.add("https://docs.jabref.org/");		
		yourList.add("https://github.com/joelparkerhenderson/git_commit_message");
		yourList.add("help.jabref.org");
		yourList.add("https://github.com/JabRef/help.jabref.org/issues");
		yourList.add("https://github.com/joelparkerhenderson/git_commit_message");
		yourList.add("https://github.com/JabRef/help.jabref.org/issues");
		yourList.add("<https://github.com/JabRef/user-documentation/issues>"); 
		yourList.add("https://github.com/JabRef/user-documentation/issues"); 		
		
		//- [ ] Change in CHANGELOG.md described
		//- [ ] Tests created for changes
		//- [ ] Manually tested changed features in running JabRef
		//- [ ] Screenshots added in PR description (for bigger UI changes)
		//- [ ] Ensured that [the git commit message is a good one](https://github.com/joelparkerhenderson/git_commit_message)
		//- [ ] Check documentation status (Issue created for outdated help page at [help.jabref.org](https://github.com/JabRef/help.jabref.org/issues)?)
		//"[ ]", "[ x ]", "[x]","change changelogmd described", "tests created changes", "manually tested changed features running jabref", "screenshots added pr description bigger ui changes", "ensured [ git commit message good one ]", "httpsgithubcomjoelparkerhendersongitcommitmessage", "check documentation status issue created outdated help page", "httpsgithubcomjabrefhelpjabreforgissues"]
		//<!-- describe the changes you have made here: what  why  ...       Link issues by using the following pattern: [#333](https://github.com/JabRef/jabref/issues/333) or [koppor#49](https://github.com/koppor/jabref/issues/47).      The title of the PR must not reference an issue  because GitHub does not support autolinking there. -->  Fixes the delete action in the maintable branch and the  do you really want to delete the entry -dialog is converted to JavaFX. Moreover  a few lines of JavaFX-Swing-interaction code in  FXDialog  are deleted since it is no longer needed.  ----  - 
			//[ ] Change in CHANGELOG.md described - [ ] Tests created for changes - [x] Manually tested changed features in running JabRef - [ ] Screenshots added in PR description (for bigger UI changes) - 
			//[ ] Ensured that [the git commit message is a good one](https://github.com/joelparkerhenderson/git_commit_message) - [ ] Check documentation status (Issue created for outdated help page at [help.jabref.org](https://github.com/JabRef/help.jabref.org/issues)?)	    

		//boolean isFirst = true;
		
		for (int i =0; i<yourList.size(); i++) {
			String text = yourList.get(i).toLowerCase();
			int pos = newstr.indexOf(text) ;
			
			if (pos != -1) {
				String temp1 = "";
				int tam = text.length();
				//if (isFirst) {
					//temp1 = str.substring(0, pos+tam-1);
					//isFirst = false;
				//} else {
					temp1 = newstr.substring(0, pos);
				//}
				
				newstr = temp1 + newstr.substring(pos+tam, newstr.length());
			}
		}
		return newstr;
	}


	private void insertApriori(ArrayList<String> api) 
	{
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);

		for(int i = 0; i<api.size(); i++) 
		{
			boolean result = fd.insertApriori(pr, java, api.get(i), project);
			
			if (!result) 
			{
				System.out.println("Insert apriori failed: "+project +" - "+ pr + " - "+ java + " - "+ api.get(i));
			}
		}
		
	}
	
	private void insertPr() {
		// TODO Auto-generated method stub
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		
		boolean result = fd.insertPr(pr, title, body, project);
		if (!result) {
				System.out.println("Insert pr failed: "+ pr + " - "+ title + " - "+ body);
		}
		
		
	}


	private ArrayList<String> findAPI(String pr2, String java2, String projectName) {

		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		ArrayList<String> gs = fd.buscaAPI(pr2, java2, projectName);

		if (gs==null) {
			System.out.println("pr: "+pr+" - "+java+" not found in database!!!");
		}
		return gs;
	}


	private boolean splitLine(String s) {

		boolean isOk = false;
		int comma = s.indexOf(separator);
		
		if (comma == -1) 
		{
			System.out.println(" line with problems:  first separator missing...");
			return isOk;
		}
		
		pr = s.substring(0, comma);
		int comma1 = s.indexOf(separator, comma+1);
		
		if (comma1 == -1) 
		{
			System.out.println(" line with problems:  second separator missing...");
			return isOk;
		}
		
		java = s.substring(comma+1, comma1);
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		int slash = java.lastIndexOf("/");
		
		if (slash == -1) 
		{
			System.out.println(" line with problems:  path slash missing...");
			return isOk;
		}
		
		java = java.substring(slash+1, java.length());
		int comma2 = s.indexOf(separator, comma1+1);
		
		if (comma2 == -1) 
		{
			System.out.println(" line with problems:  third separator missing...");
			return isOk;
		}
		
		title = s.substring(comma1+1, comma2);
		body = s.substring(comma2+1, s.length());
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		//int slash = s.lastIndexOf("/");
		//java = s.substring(slash+1, s.length());
		pr = pr.trim();
		
		if (pr.equals("884")||pr.contentEquals("96")) 
		{
			System.out.println("debug");
		}
		
		java = java.trim();
		title = filter_text(title);
		
		body = filter_text(body);
		System.out.println("pr: "+pr+" , java: "+java + " title: "+ title);
		
		isOk = true;
		
		return isOk;
		
	}

}
