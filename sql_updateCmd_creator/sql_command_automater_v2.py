#--------------------------------------------------------------------------- 
# Author : Jacob Penney 
# Purpose: 
# Process: 
# Notes  : 
#--------------------------------------------------------------------------- 

import argparse


CMD_FRMT = "UPDATE \"API_specific_backup\" SET expert = \'%s\' WHERE general = \'%s\' AND specific = \'%s\';"




#--------------------------------------------------------------------------- 
# Name   : 
# Context: 
# Process: 
# Params : 
# Output : 
# Notes  : 
# Docs   : 
#--------------------------------------------------------------------------- 
def main():
    command_filename = get_CLI_args()

    cmd_metalist = read_csv( command_filename )

    cmd_str_list = create_cmd_str( cmd_metalist )

    write_sql( cmd_str_list, command_filename )




#--------------------------------------------------------------------------- 
# Name   : 
# Context: 
# Process: 
# Params : 
# Output : 
# Notes  : 
# Docs   : 
#--------------------------------------------------------------------------- 
def create_cmd_str( cmd_metalist ):
         
    cmd_str_list  = []
    expert_index  = 0
    general_index = 0
    specific_index = 0
    header_list   = cmd_metalist[0]
    header_index         = 0


    while header_index < len( header_list ):
        if header_list[header_index] == "general":
            general_index = header_index

        else:
            if header_list[header_index] == "expert":
                expert_index = header_index

            else:
                if header_list[header_index] == "specific":
                    specific_index = header_index

        header_index += 1


    for list in cmd_metalist[1:]:
        general_item = list[general_index]
        expert_item  = list[expert_index]
        specific_item = list[specific_index]

        if expert_item.lower() not in { "lixo", "trash" }:
            cmd_str = CMD_FRMT %( expert_item, general_item, specific_item )

            cmd_str += '\n' 

            cmd_str_list.append( cmd_str )

    
    cmd_str_list.sort()

    return cmd_str_list




#--------------------------------------------------------------------------- 
# Name   : 
# Context: 
# Process: 
# Params : 
# Output : 
# Notes  : 
# Docs   : 
#--------------------------------------------------------------------------- 
def get_CLI_args():

    # establish positional argument capability
    arg_parser = argparse.ArgumentParser( description="" )
    
    # add repo input CLI arg
    arg_parser.add_argument( 'csv_file', type=str, help="csv file name" ) 

    # retrieve positional arguments
    csv_filename = arg_parser.parse_args().csv_file


    return csv_filename




#--------------------------------------------------------------------------- 
# Name   : 
# Context: 
# Process: 
# Params : 
# Output : 
# Notes  : 
# Docs   : 
#--------------------------------------------------------------------------- 
def read_csv( csv_file ):

    cmd_metalist = []

    try:
        csv_file_obj = open( csv_file, 'r' )

    except FileNotFoundError:
        print( "\nCSV file not found!\n" )  

    else:
        cmd_data = csv_file_obj.readlines()

        for line in cmd_data:
            nl_stripped_line = line.strip( '\n' )
            cmd_list = nl_stripped_line.split( ',' )

            cmd_metalist.append( cmd_list )


        csv_file_obj.close()

        return cmd_metalist




#--------------------------------------------------------------------------- 
# Name   : 
# Context: 
# Process: 
# Params : 
# Output : 
# Notes  : 
# Docs   : 
#--------------------------------------------------------------------------- 
def write_sql( cmd_list, command_filename ):

    end_str = "--End of input \"%s\"\n\n" %( command_filename ) 
    out_filename = "outputs/cmd_output.sql"


    output_file = open( out_filename, 'a' )

    for line in cmd_list:
        
        output_file.write( line )


    output_file.write( end_str )

    output_file.close()




if __name__ == "__main__":
    main()  
