#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <sys/stat.h>

#include <stdlib.h>
#include <stdio.h>
#include <errno.h>

/* Ausgabe der Pseudo-Datei /proc/self/maps,
 * die eine Aufteilung des virtuellen Adressraumes darstellt. 
 * Mit msg sollten Sie eine eigene Nachricht (einen String) 
 * uebergeben, die Ihnen anzeigt was Sie gerade ausgeben 
 * (z.B. "Virtueller Adressraum vor der Summenberechnung")
 */
void print_memory_map (char *msg) {

    int fd;
    char charBuffer[1024];
    int parsedChars;

    printf("%s\n", msg);

    fd = open("/proc/self/maps", O_RDONLY);

    //while( (parsedChars = read(fd, charBuffer, 1024)) > 0) {
    //    write(1, charBuffer, 1024);
    //}
    do {
        parsedChars = read(fd, charBuffer, 1024);
        write(1, charBuffer, parsedChars);
    } while (parsedChars > 0);

    printf("\n\n");

    close(fd);

}


/* Berechnung der Summe aller Bytes einer Datei (Standard-Version)
 * Hierbei bitte "Standard" Datei-Operationen verwenden.
 * D.h. open(), read(), stat(), close(). Nutzen Sie auch die
 * Funktion print_memory_map(), um vor der Summenberechnung
 * den virtuellen Adressraum auszugeben. Die Funktion soll
 * als Rueckgabewert die Summe liefern und der Parameter
 * filename gibt den zu oeffenden Datei-Namen an.
 * Speichern Sie die den Inhalt der Datei in einem mit malloc angelegten 
 * Array (unsigned char) bevor Sie die Summe berechnen. 
 */
int byte_sum (char *filename) {
    
    int i;
    int sum;
    int fd;
    int fileSize;
    unsigned char* fileBuffer;

    fd = open(filename, O_RDONLY);

    struct stat st;
    stat(filename, &st);
    fileSize = st.st_size;

    fileBuffer = (unsigned char*) malloc(sizeof(char)*fileSize);
    read(fd, fileBuffer, fileSize);

    sum = 0;
    for(i = 0; i < fileSize; i++) {
        //printf("%d: %d \t %c %d\n", i, fileBuffer[i], (char)(fileBuffer[i]&255), sum);
        sum += fileBuffer[i];
    }

    free(fileBuffer);
    close(fd);

    return sum;

}


/* Berechnung der Summe aller Bytes einer Datei (mmap-Version)
 * In dieser Version nutzen Sie die in der Vorlesung kennengelernten
 * Funktionen, um die Datei im Speicher einzublenden. Betrachten Sie
 * hier den virtuellen Adressraum bevor und nachdem Sie die Datei mit
 * mmap() eingeblendet haben. An sonsten
 * soll sich die Funktion genauso verhalten wie byte_sum().
 */
int byte_sum_mmap (char *filename) {

    int i;
    int fd;
    int fileSize;
    int *map;
    int sum;

    fd = open(filename, O_RDONLY);

    struct stat st;
    stat(filename, &st);
    fileSize = st.st_size;

    map = mmap(0, fileSize, PROT_READ, MAP_SHARED, fd, 0);
    
    sum = 0;
    i = 0;
    while(i<fileSize) {
        sum  +=  map[(i/4)] >> (8*(i%4))  & 255; 
        i++;
    }

    munmap(map, fileSize);
    close(fd);

    return sum;

/** Debug message and explanation for while-loop:
 *
 * printf("%d: %c\n", (i/4)+(i%4), (char)((map[(i/4)]>>((i%4)*8)) & 255));
 *
 *
 * map[] contains our characters, but is laid out as an int map.
 * Because a char consists of 1Byte and and int consist of 4Bytes,
 * we need to have a look at 4 different Bytes per map entry:
 *
 *   The slot we're looking at in every iteration will be map[(i/4)]. Because i/4 is floored,
 *   its result will be the same for 4 iterations in a row. 
 *   In these 4 iterations, we shift the contents by 0bit, 8bit, 16bit, and 24bit, hence (i%4)*8 .
 *   In order to convert the shifted int into an 8bit format, we use '& 255'.
*/
}



int main (int argc, char *argv[]) {

  int sum;
  if (argc <= 1) {
    printf ("Aufruf: %s <Dateiname>\n", argv[0]);
    return 3;
  }

  print_memory_map("\n\n----- Adressraum vorher -----");

  sum = byte_sum(argv[1]);
  printf("### Summe aus byte_sum():\t%i\n", sum);

  print_memory_map("\n\n----- Adressraum nach byte_sum -----");

  sum = byte_sum_mmap(argv[1]);
  printf("### Summe aus byte_sum_mmap():  %i\n", sum);

  print_memory_map("\n\n----- Adressraum nach byte_sum_mmap()-----");

  return 0;
}