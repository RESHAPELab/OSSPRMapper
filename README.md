# How to Run

# arguments example
postgres
admin
jabref50
devmay2020
/Users/fd252/Documents/Cursos3/NAU/Research2/Python/data/filesPR3BodyTitle2.txt
/Users/fd252/Documents/Cursos3/NAU/Research2/Python/data/aprioriBodyTitle.csv
1
;
/Users/fd252/Documents/Cursos3/NAU/Research2/Python/data/binaryBodyTitle.csv
/Users/fd252/Documents/Cursos3/NAU/Research2/Python/data/PRClasses.txt

# meaning

		user = args[0];
		pswd = args[1];
		project = args[2];
		db = args[3];
		file = args[4];
		csv = args[5];
		isOnlyCSV = Integer.parseInt(args[6]);
		separator = args[7];
		bin = args[8];
		classes = args[9];
		if (isOnlyCSV==1) {
			getPrs(); // apriori body title
			genBinaryExit(); //binary body title
		}
		else {
			readData();
		}
	}

# if the isOnlyCSV argument is 1 the program will generate the bin file on argument 8 using the info on the database
# if the isOnlyCSV argument is 0 the program will read the input, update the database and generates the csv and classes files (arguments 5 and 9)
