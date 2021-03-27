#include <stdio.h>
#include <stdlib.h>

#include "speicher.h"

//************************************************************************
//	Version 3, May30th 2pm
//
//  Purpose:
//  	This file's functions add up to a very simplistic rendition of malloc.c
//
//
// 	Changes:
//      o Corrected pointer arithmetic regarding mein_speicher[]; sp_struct->zeiger now
//        correctly points to memory adresses rather than just containing an int representing
//        the array slot.
//		o Replaced byte_belegen(adress) and byte_frei_machen(adress) by more powerful methods
//		  (bytes_belegen(adress, offset) and bytes_frei_machen(adress, offset)).
//
//
//
//  Questions:
//      o Subtracting &mein_speicher from an int yields a compiler warning about
//        the former being of type char* (e.g. int i = 50 - &mein_speicher).
//        I thought "&variableName" would always come back with 32 bits representing
//        the variable's virtual adress (i.e. an int), so, what gives?
//                                            
//************************************************************************

/* der folgende Speicherbereichi (heap) soll verwaltet werden */
static char mein_speicher[MAX_SPEICHER]; //1024 chars


/* folgendes ist eine Freiliste, eine 1 entspricht 
entsprechendes Byte im Speicher ist frei, eine 0 bedeutet 
das Byte ist belegt */
static unsigned char freiliste[MAX_SPEICHER / 8]; //128 chars = 1024 bits auf 0 oder 1


/* folgende Funktion gibt die Freiliste aus (64 Byte pro Zeile) */
void freiliste_ausgeben () {
   int n = 0;
   int m = 0;
   int bytes = 0;
   int shift = 0;
   printf("\t 0       1       2       3       4       5       6       7       \n"); 

   for(n = 0; n < MAX_SPEICHER/8; n++) {
      if(bytes == 0) printf("%d:\t ", n * 8);
      for(shift = 0;shift < 8; shift++) {
         if (((freiliste[n]) & (1 << shift)) > 0)
            printf("1");
         else
            printf("0");
      }
      bytes++;
      if(bytes == 8) {
         printf("\n");
         bytes = 0;
      }
   }
   printf("\n");
}


/* Diese Funktion ueberprueft, ob das Byte byte_index in der Freiliste gesetzt ist 
   (frei, Rueckgabewert 0) oder auf 0 gesetzt ist (belegt, Rueckgabewert 1). Bei
   Fehlern gibt die Funktion eine Wert < 0 zurueck. */
static int byte_belegt(unsigned int byte_index) {
   if(byte_index < 0 || byte_index >= MAX_SPEICHER)
      return -1;
   
   //Vom Byte muss zurest auf das bit umgerechnent werden
   int byte = byte_index / 8;
   int bit = byte_index % 8;

   if(freiliste[byte] & (1 << bit))
      return 0;
   return 1; 
}


/* Mark <offset> amount of bytes as used, starting at byte_index. */
static void bytes_belegen(unsigned int byte_index, int offset) {
   if(byte_index < 0 || byte_index+offset >= MAX_SPEICHER)
      return;

   int i;
   for(i = byte_index; i <= byte_index+offset; i++) {
       int byte = i / 8;
       int bit = i % 8;
       freiliste[byte] = (freiliste[byte] ^ (1 << bit));
    }
}

/* Mark <offset> amount of bytes as unused, starting at byte_index. */
static void bytes_frei_machen(unsigned int byte_index, int offset) {
   if(byte_index < 0 || byte_index+offset >= MAX_SPEICHER)
      return;

   int i;
   for(i = byte_index; i <= byte_index+offset; i++) {
       int byte = i / 8;
       int bit = i % 8;
       freiliste[byte] = (freiliste[byte] | (1 << bit));
    }
}



/* Diese Funktion soll sp_struct->groesse Bytes im Speicher belegen und die 
First Fit Strategie implementieren. Der Rueckgabewert soll >0 sein, wenn die Anforderung 
erfolgreich war, ansonsten 0 (kein genuegend grosser zusammenhaengender Bereich frei). */ 
int mein_alloc(speicher_struct * sp_struct) {

    int adress = 0;  //points to starting adress of potentially new block.
    int window = 0;  //counts amount of free slots between used blocks.

	unsigned int i;
    // Loop over all bytes in memory table. Keep count of subsequent unused bytes
	// (in "window"). "adress" points to the start of the current unused block.
    for(i=0; i < MAX_SPEICHER; i++) {
        if(byte_belegt(i)) {
            window = 0;
            adress = i+1;
        } else {
            window+=1;
        }
        // If potential block is as large as current sp_struct's size, allocation is possible.
        // Set sp_struct's pointer, then mark the bytes it covers as "used".
        if(window==sp_struct->groesse) {
            bytes_belegen(adress, window-1);
            sp_struct->zeiger = &mein_speicher[adress];
            return 1;
        }
    }
    
    // return 0 if there is insufficient memory space and allocation is impossible.
    return 0;
}


/* Diese Funktion soll sp_struct->groesse Bytes, beginnend von sp_struct.zeiger an frei 
geben. Dabei soll der sp_struct->zeiger wieder auf NULL gesetzt werden als Zeichen dafuer,
dass die Struktur keine Speicher haelt. */
void mein_free(speicher_struct * sp_struct) {

	// Mark all bytes between sp_struct's pointer and pointer+offset as unused ...
    int start = (int)sp_struct->zeiger - (int)&mein_speicher; //translates zeiger's position in mein_speicher to an array slot in freiliste[]
    int offset = (sp_struct->groesse)-1; //subtract 1 because groesse already includes the pointer adress
    bytes_frei_machen(start, offset); //marks bits in freiliste[] as free

    // ... then set its pointer to NULL.
    sp_struct->zeiger = NULL;

}



/* Diese Funktion initialisiert die Freiliste */
void mein_init() {
  memset(freiliste, 255, MAX_SPEICHER/8);
}

/* Diese Funktion vergleicht die erwartete Freiliste mit der tatsaechlichen */
int speicher_vergleich () {
   unsigned char freiliste_erwartet[MAX_SPEICHER/8];
   memset(freiliste_erwartet, 255, MAX_SPEICHER/8); 
   memset(freiliste_erwartet, 0, 6);
   freiliste_erwartet[6] = (char) 252;
   freiliste_erwartet[8] = (char) 63;
   freiliste_erwartet[13] = (char) 63;
   memset(&freiliste_erwartet[9], 0, 1);
   memset(&freiliste_erwartet[14], 0, 12);
   freiliste_erwartet[26] = 252;
   freiliste_erwartet[36] = 3;
   freiliste_erwartet[41] = 252;
   memset(&freiliste_erwartet[37], 0, 4);

   int n = 0;
   for(n = 0; n < MAX_SPEICHER/8; n++) {
      if(freiliste[n] != freiliste_erwartet[n]) {
         printf("Fehler in byte %d der Freiliste.\n", n);
         return 0;
      }
   }
   return 1;
}

