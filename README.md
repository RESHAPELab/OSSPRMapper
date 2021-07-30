### How to Run

#### Args example


#### Complementary source code

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

##### Note on the isOnlyCsv arg:
- if the isOnlyCSV argument receives

    - 0: the program will read in the associated input (arg four, "file"), update the database (arg three, "db"), and generate the csv and classes output files (args five and nine, respectively). 

    - 1: the program will generate the binary output file (arg eight, "bin") using the info in the database.

