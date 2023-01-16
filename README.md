OSSPRMapper is a dual goal java program used to update the database (PR table) with the files (source code files) updated by each PR and to write the binary files for the machine learning processing. 

isOnlyCSV==0 -> It populates the PR table

Requeriment: Parser should run before OSSPRMapper and tables file, file_APi and API should be populated. 
https://github.com/NAU-OSL/openSourceParser

Database should exist:
https://github.com/NAU-OSL/PipelineDB

Namespaces should have parsed:
https://github.com/fabiojavamarcos/parseAPIPath

expert API evaluation should run before OSSPRMapper.

isOnlyCSV==1 -> It writes the binary files

Requeriment: ETL2, ETL1, Parser and OSSPRMapper with isOnlyCSV = 0 should run before OSSPRMapper with isOnlyCSV = 1.
https://github.com/NAU-OSL/ETL2-Pipeline
https://github.com/NAU-OSL/ETL1-Pipeline

For task's skills the program will update the PR table with pr, title, body and project. For the author's skills it also fills the column author. One database can hold data for many projects, however we recommend using separated databases for taks and author's skills.

For binary files genaration the programm will read the PR, PR_issues and apriori and api-specific tables to genenate the binary files. 

Versions:

For multi programming languages support (beyond java): 
https://github.com/fabiojavamarcos/OSSPRMapper2 

For Social metrics:
https://github.com/fabiojavamarcos/OSSPRMapper2-social

For authpor's skills:
https://github.com/fabiojavamarcos/OSSPRMapper4

Input: 
filesPR3BodyTitle2.txt 
arguments described below. 

Format example:
https://1drv.ms/t/s!AguSR5HRUapeh6NLYXPq0gANf1k_cg?e=80cWFJ

Output:
PR table populated. (isOnlyCSV==0)
binary files. (isOnlyCSV==1)

Output examples:
Look folder: OSSPRMapper/outputs/


### How to Run

#### Args example
![image](https://user-images.githubusercontent.com/59481467/128212226-c3724885-a0dd-41e7-8779-b7d961c9bd02.png)



#### Relevant source code

		user        = args[0];
		pswd        = args[1];
		project     = args[2];
		db          = args[3];
		file        = args[4];
		csv         = args[5];
		isOnlyCSV   = Integer.parseInt(args[6]);
		separator   = args[7];
		bin         = args[8];
		classes     = args[9];
		if (isOnlyCSV==1) {
			getPrs(); // apriori body title
			genBinaryExit(); //binary body title
		}
		else {
			readData();
		}
	}

Updated arguments example:

postgres

123

audacity

audacity_cpp

filesPR3BodyTitle2

aprioriBodyTitle.csv

1

;

binaryBodyTitle.csv

PRClasses.txt

/Users/fd252/OneDrive/Production/ETL1-Pipeline-main/data/outputs/new/

/Users/fd252/OneDrive/Production/OSSPRMapper-master/outputs/![OSL_pipeline-inputs](https://user-images.githubusercontent.com/34105280/212755811-b752918c-f65a-4ef8-8ce0-0c76174f08e1.png)


##### Note on the isOnlyCsv arg:
- if the isOnlyCSV argument receives

    - 0: the program will read in the associated input (arg four, "file"), update the database tables apriori and pr (arg three, "db"), and generate the csv and classes output files (args five and nine, respectively). 

    - 1: the program will generate the binary output file and the apriori file in the folder (arg eight, "bin") using the info in the database.

