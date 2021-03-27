/* Praktikum 1 - Eine Shell programmieren
 *
 * Der Programmschnipsel hier ist das grobe Geruest fuer eine
 * _sehr_ einfach gestrickte Shell. Die wichtigen Teile fehlen 
 * allerdings noch. Ihre Aufgabe ist es den Quell-Code zu einer
 * vollwertigen Shell zu vervollstaendigen. Die Anforderungen
 * an Ihre Implementierung sind die folgenden:
 *
 * 1) Die Shell soll fuer jedes eingegebene Kommando einen 
 *    Kindprozess erzeugen und auf die Beendigung dieses 
 *    Prozesses warten, bevor weitere Kommandos angenommen
 *    werden. (POSIX fork(), wait(), ...). Bevor Sie loslegen
 *    lesen Sie die man pages zu fork() und wait()
 * 2) Der Kindprozess soll das eingegebene Kommando ausfuehren.
 *    Nutzen Sie dazu z.B. execve(). Lesen Sie dazu die man
 *    pages. Hinweis: die Argumente fuer die Funktion execve
 *    werden von diesem Skelett schon passend erstellt! Sie
 *    koennen aber im Prinzip beliebige Varianten der 
 *    exec Familie verwenden.
 * 3) Die Shell soll Strg+c ignorieren, d.h. auch bei der
 *    Eingaben von Strg+c soll die Shell normal weiterlaufen,
 *    d.h. nicht abbrechen. (POSIX signal() - man page lesen)
 * 4) Die Shell soll das Kommando cd (change directory)
 *    und exit (d.h. die Shell beenden) implementieren. Tipp:
 *    implementieren Sie erst die Funktion exit.
 * 5) Die Shell sollte moeglichst fehlerfrei laufen. Finden
 *    Sie das Speicherleck ;)
 * 
 * Hilfsfunktionen, die nützlich sein könnten sind:
 *    - strcmp(): zwei Strings auf Gleichheit überprüfen 
 *    - chdir(): das aktuelle Verzeichnis wechseln
 *
 * Ziel dieser Uebung soll es sein, dass Sie...:
 * 
 * a) lernen mit den POSIX Funktionen umzugehen
 * b) lernen mit den man pages umzugehen
 * c) C pointer und den Umgang damit verstehen
 * d) Prozesserzeugung erlernen 
 * c) Speicherverwaltung in C verstehen
 *
 * Wenn Sie mit dem Programmieren fertig sind, dann probieren 
 * Sie bitte folgende Befehle in dieser Reihenfolge in 
 * Ihrer Shell aus: 
 *
 * ls
 * ls -l
 * pwd
 * cd ..
 * pwd
 * echo 1
 * cat <Dateiname ihrer Wahl>
 * Strg+c
 * "nur <enter>"
 * floobaroo
 * exit
 *
 * Bevor Sie die Shell mit exit beenden, schauen Sie mal mit dem 
 * Kommando ps nach, wieviele Instanzen Ihrer Shell laufen 
 * (es sollte nur eine sein).
 *
 * Sollten Sie die gewohneten Ausgaben sehen,
 * sollte die Shell stabil laufen und sollten Sie kein Speicherleck
 * haben, erst dann laden Sie Ihre Version im Moodle hoch 
 * (benutzen Sie folgende Namenskonvention fuer die Datei:
 * shelly_vorname_nachname.c).
 *
 * Abschliessend beantworten Sie die Fragen in der dazugehoerigen 
 * Moodle-Lektion.
 *
 * Viel Erfolg!
 * 
 * Rolf Winter, 07.04.2013
 */


#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>

#define MAX_CMD_LEN 2048
#define MAX_ARG_NUM 128 


/* Gibt den prompt auf stdout aus
 */

void prompt() {

  printf("\nshelly] ");
  fflush(stdout); //what exactly does flushing do? 
}


/* Durchsucht die Pfade in der PATH Variablen
 * nach dem Kommando cmd und returnt den pfad
 * zum Kommando.
 */

char* expandCmd(char* cmd) {
  char* path = getenv("PATH");
  char* path_copy = NULL;
  char* delim = ":";
  char* token = NULL;
  struct stat buf;
  char* expandedCmd = NULL; 
  int len = 0;

  if(path == NULL || strlen(cmd) == 0){
    //printf("Looking for empty command. Returning Null.\n");
    return NULL;
  }

  //write all env paths to path_copy
  path_copy = (char*) malloc((strlen(path) + 1) * sizeof(char));  
  strncpy(path_copy, path,  strlen(path));
  token = strtok(path_copy, delim);   

  //loop through all env paths...
  while(token != NULL) {
    len = strlen(token) + strlen(cmd) + 2; 
    expandedCmd = (char*) malloc(len * sizeof(char));
    memset(expandedCmd,0, len);
    strcat(expandedCmd, token);
    strcat(expandedCmd, "/");
    strcat(expandedCmd, cmd);
    //... and check if the requested command has a corresponding
    //executable file in that directory
    if(stat(expandedCmd, &buf) == 0) {
      free(path_copy);
      return expandedCmd;
    }
    //if not, repeat while loop for the next env path
    free(expandedCmd);
    token = strtok(NULL, delim);
  }
  
  //if command was not found in any env path, return null.
  free(path_copy);
  return NULL;
}


/* Einstiegspunkt in die Shell
 */

int main(int argc, char* argv[], char* envp[]) {
 

  signal(SIGINT, SIG_IGN); //possible TODO: this prints C^ everytime an interrupt is attempted by the user
	
  char c_in;   //in c_in landen die Zeichen die die Shell entgegennimmt
 
  char* args[MAX_ARG_NUM];   //args enthaelt pointer zu den einzelen Argumenten eines eingegebenen
  			     //Befehls. args[0] enthaelt den Befehl selbst

  char* full_cmd = (char*) malloc(MAX_CMD_LEN * sizeof(char));   //in full_cmd wird der gesamte Befehl selbst abgespeichert 
  
  char* cmd;   //in cmd wird der Befehl plus Pfad separat abgelegt
  
  int curr_cmd_len = 0;   //die aktuelle Laenge des Befehls (waehrend der Eingabe in die Shell)
  
  int chld_pid = 0;   //die Prozess ID des zu erzeugenden Kind-Prozesses

  char* cmd_pnt = full_cmd; //Zeiger auf die aktuelle Position des zu schreibenden Zeichens 

  int curr_arg = 0;   //dezeit zu schreibendes Argument (in args)

  args[0] = full_cmd;   //der erste pointer zeigt auf den Anfang von full_cmd (der Befehl selbst)

  //den String full_cmd mit Nullen fuellen
  memset(full_cmd, 0, MAX_CMD_LEN);
  prompt();

  //die Shell soll ewig weiterlaufen...
  //in einem Schleifendurchlauf wird ein Zeichen von der Kommandozeile 
  //eingelesen und evtl. die obigen Datenstrukturen angepasst
  while(1) {
  	//If the command is too long at this point...
	if(curr_cmd_len >= MAX_CMD_LEN) {
	//...reset the while loop and continue.
		printf("Command too long.\n");
		curr_cmd_len = 0;
		args[0] = full_cmd;
		cmd_pnt = full_cmd;
		curr_arg = 0;
		prompt();
		memset(full_cmd, 0, MAX_CMD_LEN);
		continue;
	}


	c_in = getchar(); //copies next input stream character to c_in

	if(c_in == ' ') { //Special Case: Space detected
		if(curr_cmd_len == 0 || *(cmd_pnt - 1) == ' ')	//If command started with space OR there's a duplicate space, continue
			continue; 
		//Space detection implies another argument after the space.
		//Check if there'll be too many arguments (careful: last slot
		//is reserved for NULL, else execve won't work)
		if(curr_arg + 1 >= MAX_ARG_NUM - 1) {
			//if there's too many arguments, reset and continue.
			printf("Too many arguments.\n");
			curr_cmd_len = 0;
			args[0] = full_cmd;
			cmd_pnt = full_cmd;
			curr_arg = 0;
			prompt();
			memset(full_cmd, 0, MAX_CMD_LEN);
		} else { //this else is jumped to if there was a space
			//TODO: wtf does *cmd_pnt=\0 achieve? does this terminate the previous command/parameter with \0 ?
			*cmd_pnt = '\0';     
			cmd_pnt++;
			curr_arg++;
			curr_cmd_len++;
			args[curr_arg] = cmd_pnt;
		} 

	//Special case #2: User pressed enter, i.e. command is supposed to be executed:
    	} else if(c_in == '\n') { 
		//at this point, args[] should contain the full user call.
		//fullcmd only contains the executable name (as found in args[0])	
		args[++curr_arg] = NULL; //TODO: why?

		//Special Commands: exit, cd, <else>
		if(strcmp(args[0],"exit") == 0) {
			break;
		} else if (*args[0] == (int) NULL) { //"if user just hits enter"
			curr_cmd_len = 0;
			args[0] = full_cmd;
			cmd_pnt = full_cmd;
			curr_arg = 0;
			memset(full_cmd, 0, MAX_CMD_LEN);
			prompt();
			continue;
		} else if(strncmp(args[0], "cd", 2) == 0) {
			//full_cmd must include the entire command line at this point.
			//printf("Detected cd call. Argument was: %s %s",args[0], args[1]);
			chdir(args[1]);
			prompt();
			curr_cmd_len = 0;
			args[0] = full_cmd;
			cmd_pnt = full_cmd;
			curr_arg = 0;
			memset(full_cmd, 0, MAX_CMD_LEN);
			continue;
      		} else {
			//If user doesnt want to exit or cd, check if the user's command exists in the environment variable...
			//printf("Looking for command \"%s\"... ", args[0]);
			cmd = expandCmd(args[0]);

			//... if not, TODO reset! and continue:
			if(cmd == NULL) {
				printf("Could not find command \"%s\".\n", args[0]);
				//fflush(stdout); //we have to flush here oO - why does the flush in prompt() not suffice? edit: seems to work ok now. weird
				//prompt();
				//continue;
			} else {
				//printf("Found command \"%s\".\n", cmd);
				if((chld_pid = fork()) < 0) {
					fprintf(stderr, "Fehler... %s\n", strerror(errno));
				} else if(chld_pid == 0) { //everything in here is executed in the child process
					//printf("      Child: Hi!\n");
					//printf("      Child: Calling execve with parameters: %s, %s, %s\n", cmd, args[1], envp[0]);
					execve(cmd, args, envp);
					break;
				} else { //everything in here is executed in the original process
					//printf("Shelly Base: Hi!\n");
					wait(0); //waits until child is killed TODO: really? check man page again... yup, appears to
					//printf("Shelly Base: Child is finished. Resuming...\n");
				}
			}
			free(cmd); //memory leak fixed
			prompt();
			curr_cmd_len = 0;
			args[0] = full_cmd;
			cmd_pnt = full_cmd;
			curr_arg = 0;
			memset(full_cmd, 0, MAX_CMD_LEN);
			continue;
		}
	} else {
		*cmd_pnt = c_in;
		cmd_pnt++;
		curr_cmd_len++;
	}
  }

  return 0;
}

