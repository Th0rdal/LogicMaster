#include "bitboards.h"
#include <stdio.h>
const uint64_t fileA = 0x8080808080808080ULL;
const FileBitboards fileBitboards = {
                .fileA = fileA,
                .fileB = fileA >> 1,
                .fileC = fileA >> 2,
                .fileD = fileA >> 3,
                .fileE = fileA >> 4,
                .fileF = fileA >> 5,
                .fileG = fileA >> 6,
                .fileH = fileA >> 7,
                .size = 8,
                .boards = {
                    fileA,
                    fileA >> 1,
                    fileA >> 2,
                    fileA >> 3,
                    fileA >> 4,
                    fileA >> 5,
                    fileA >> 6,
                    fileA >> 7,}
                };


const uint64_t rank1 = 0x00000000000000FFULL;
const RankBitboards rankBitboards = {
                .rank1 = rank1,
                .rank2 = rank1 << 8,
                .rank3 = rank1 << 16,
                .rank4 = rank1 << 24,
                .rank5 = rank1 << 32,
                .rank6 = rank1 << 40,
                .rank7 = rank1 << 48,
                .rank8 = rank1 << 56,
                .size = 8,
                .boards = {
                    rank1,
                    rank1 << 8,
                    rank1 << 16,
                    rank1 << 24,
                    rank1 << 32,
                    rank1 << 40,
                    rank1 << 48,
                    rank1 << 56}
                };

const DiagonalBitboards diagonalBitboards = {
                    .diagonal1 = 0x8000000000000000ULL, //A8
                    .diagonal2 = 0x4080000000000000ULL, //A7-B8
                    .diagonal3 = 0x2040800000000000ULL, //A6-C8
                    .diagonal4 = 0x1020408000000000ULL, //A5-D8
                    .diagonal5 = 0x0810204080000000ULL, //A4-E8
                    .diagonal6 = 0x0408102040800000ULL, //A3-F8
                    .diagonal7 = 0x0204081020408000ULL, //A2-G8
                    .diagonal8 = 0x0102040810204080ULL, //A1-H8
                    .diagonal9 = 0x0001020408102040ULL, //B1-H7
                    .diagonal10 = 0x0000010204081020ULL, //C1-H6
                    .diagonal11 = 0x0000000102040810ULL, //D1-H5
                    .diagonal12 = 0x0000000001020408ULL, //E1-H4
                    .diagonal13 = 0x0000000000010204ULL, //F1-H3
                    .diagonal14 = 0x0000000000000102ULL, //G1-H2
                    .diagonal15 = 0x0000000000000001ULL, //H1
                    .size = 15,
                    .boards = {
                        0x8000000000000000ULL,             
                        0x4080000000000000ULL, 
                        0x2040800000000000ULL, 
                        0x1020408000000000ULL, 
                        0x0810204080000000ULL, 
                        0x0408102040800000ULL, 
                        0x0204081020408000ULL, 
                        0x0102040810204080ULL, 
                        0x0001020408102040ULL, 
                        0x0000010204081020ULL,
                        0x0000000102040810ULL,
                        0x0000000001020408ULL,
                        0x0000000000010204ULL,
                        0x0000000000000102ULL,
                        0x0000000000000001ULL,}
                };

const DiagonalBitboards antiDiagonalBitboards = {
                        .diagonal1 = 0x0100000000000000ULL, //A8
                        .diagonal2 = 0x0201000000000000ULL, //A7-B8
                        .diagonal3 = 0x0402010000000000ULL, //A6-C8
                        .diagonal4 = 0x0804020100000000ULL, //A5-D8
                        .diagonal5 = 0x1008040201000000ULL, //A4-E8
                        .diagonal6 = 0x2010080402010000ULL, //A3-F8
                        .diagonal7 = 0x4020100804020100ULL, //A2-G8
                        .diagonal8 = 0x8040201008040201ULL, //A1-H8
                        .diagonal9 = 0x0080402010080402ULL, //B1-H7
                        .diagonal10 = 0x0000804020100804ULL, //C1-H6
                        .diagonal11 = 0x0000008040201008ULL, //D1-H5
                        .diagonal12 = 0x0000000080402010ULL, //E1-H4
                        .diagonal13 = 0x0000000000804020ULL, //F1-H3
                        .diagonal14 = 0x0000000000008040ULL, //G1-H2
                        .diagonal15 = 0x0000000000000080ULL, //H1
                        .size = 15,
                        .boards = {
                            0x0100000000000000ULL,    
                            0x0201000000000000ULL, 
                            0x0402010000000000ULL, 
                            0x0804020100000000ULL, 
                            0x1008040201000000ULL, 
                            0x2010080402010000ULL, 
                            0x4020100804020100ULL, 
                            0x8040201008040201ULL, 
                            0x0080402010080402ULL, 
                            0x0000804020100804ULL,
                            0x0000008040201008ULL,
                            0x0000000080402010ULL,
                            0x0000000000804020ULL,
                            0x0000000000008040ULL,
                            0x0000000000000080ULL,}
                        };
                   
