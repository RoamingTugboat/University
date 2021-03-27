# Dieses Programm gibt einen spezifizierten Datensatz aus.
# Usage: Auf Kommandozeile 0, 1, 2 ... eingeben.

.section .data

 inputRequest: 		.ascii "Enter the ID of the set you would like to read"
 newline:	   		.ascii "\n\0"
 filename:	  		.ascii "record.txt\0" #persistent file used for record storage
 linefeed:	   		.ascii "\r"
 user_input_length: .long 0
 record_length:     .long 140
 
.section .bss
 .lcomm databuffer, 140  	  #all purpose buffer
 .lcomm userInputBuffer, 8   #buffers requested number by user
 .lcomm fd_record, 4     	  #file descriptor of file into which records are written

.section .text

.globl _start
_start:

 movl %esp, %ebp

open_record: #Open (or create) record file in current dir
 movl $5, %eax         
 movl $filename, %ebx   
 movl $0111, %ecx      
 movl $0666, %edx       
 int $0x80

 movl %eax, fd_record  #save FD in fd_record
 
ask_for_record_ID:			#Ask which record the user would like to see
 movl $4, %eax        		#"Write"
 movl $1, %ebx         		#"Into STDOUT"
 movl $inputRequest, %ecx   #"From this address"
 movl $46, %edx				#"For this many bytes"
 int $0x80
 
read_first_answer: 			#Read answer from STDIN into buffer.
 movl $3, %eax          	#"Read"
 movl $0, %ebx          	#"From STDIN"
 movl $userInputBuffer, %ecx#"To this address"
 movl $8, %edx          	#"For this many bytes"
 int $0x80
 
 
##now, check how long user input was
#movl $0, %ebx
#movl $0, %edi
#
#check_user_input_length:
# movl $userInputBuffer(,%edi,4), %ebx
# cmpl $linefeed, %ebx
# je have_user_input_length
# 
# incl %edi
# jmp  check_user_input_length
#	
#have_user_input_length:
# movl %edi, $user_input_length

pushl $userInputBuffer
call atoi
addl %esp, $4 

#eax now contains requested value 

 #lseek: go to xth location in record file as requested by user
 movl %eax, %ecx
 mull $record_length, %ecx
 movl $19, %eax
 movl fd_record, %ebx
 movl $2, %edx
 int $0x80

#finally, print out requested record
print_record:
 movl $4, %eax          	#write
 movl fd_record, %ebx  		#from here
 movl $1, %ecx				#into stdout
 movl $140, %edx			#for 140 bytes
 int $0x80

linebreak:     
 movl $4, %eax          
 movl $0, %ebx  
 movl $newline, %ecx
 movl $1, %edx
 int $0x80

exit:
 movl $1, %eax
 movl $0, %ebx
 int $0x80