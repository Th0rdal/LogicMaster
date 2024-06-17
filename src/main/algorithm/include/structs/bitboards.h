#ifndef BITBOARDS_H
#define BITBOARDS_H

#include <stdint.h>
#include <stdio.h>

typedef struct {
   uint64_t occupancy;
   
   uint64_t pawn;
   uint64_t knight;
   uint64_t bishop;
   uint64_t rook;
   uint64_t queen;
   uint64_t king;

   uint64_t white;
   uint64_t black;
} Bitboards;

typedef struct {
    const uint64_t fileA;
    const uint64_t fileB;
    const uint64_t fileC;
    const uint64_t fileD;
    const uint64_t fileE;
    const uint64_t fileF;
    const uint64_t fileG;
    const uint64_t fileH;
    const int size;
    const uint64_t boards[8];
} FileBitboards;

typedef struct {
    const uint64_t rank1;
    const uint64_t rank2;
    const uint64_t rank3;
    const uint64_t rank4;
    const uint64_t rank5;
    const uint64_t rank6;
    const uint64_t rank7;
    const uint64_t rank8;
    const int size;
    const uint64_t boards[8];
} RankBitboards;

typedef struct { // diagonals go from left bottom to right top e.g., A1 - H8
    const uint64_t diagonal1; //A8
    const uint64_t diagonal2; //A7-B8
    const uint64_t diagonal3; //A6-C8
    const uint64_t diagonal4; //A5-D8
    const uint64_t diagonal5; //A4-E8
    const uint64_t diagonal6; //A3-F8
    const uint64_t diagonal7; //A2-G8
    const uint64_t diagonal8; //A1-H8
    const uint64_t diagonal9; //B1-H7
    const uint64_t diagonal10; //C1-H6
    const uint64_t diagonal11; //D1-H5
    const uint64_t diagonal12; //E1-H4
    const uint64_t diagonal13; //F1-H3
    const uint64_t diagonal14; //G1-H2
    const uint64_t diagonal15; //H1
    const int size;
    const uint64_t boards[15];
                          
} DiagonalBitboards;

typedef struct {
    const int size;
    const uint64_t boards[64];
} KnightMoveDictionary;

typedef struct {
    const int size;
    const uint64_t boards[64];
} KingMoveDictionary;

extern Bitboards bitboardsInit();

extern const FileBitboards fileBitboards;
extern const RankBitboards rankBitboards;
extern const DiagonalBitboards diagonalBitboards;
extern const DiagonalBitboards antiDiagonalBitboards;
extern const KnightMoveDictionary knightMoveDictionary;

#endif
