#include "structs/bitboards.h"

/*
 * creates and initializes a bitboard struct. All values will be initialized with 0.
 *
 * @return: initialized bitboard
 *
 */
Bitboards bitboardsInit() {
    Bitboards bitboards;
    bitboards.occupancy = 0;
    bitboards.pawn = 0;
    bitboards.knight = 0;
    bitboards.bishop = 0;
    bitboards.rook = 0;
    bitboards.queen = 0;
    bitboards.king = 0;
    bitboards.color[0] = 0;
    bitboards.color[1] = 0;
    bitboards.possibleMovesAfterCheck = 0xFFFFFFFFFFFFFFFF;
    return bitboards;
}

// variable to initialize fileBoards. not actually used anywhere else
static const uint64_t fileA = 0x8080808080808080ULL;

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
        fileA >> 7,
    },
    .reverseBoards = {
        ~fileA,
        ~fileA >> 1,
        ~fileA >> 2,
        ~fileA >> 3,
        ~fileA >> 4,
        ~fileA >> 5,
        ~fileA >> 6,
        ~fileA >> 7,
    }
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
        rank1 << 56
    },
    .reverseBoards = {
        ~rank1,
        ~rank1 << 8,
        ~rank1 << 16,
        ~rank1 << 24,
        ~rank1 << 32,
        ~rank1 << 40,
        ~rank1 << 48,
        ~rank1 << 56
    },
    .pawnPromotionRank = {1, 8},
    .pawn2SquareMoveRank = {7, 2},
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
        0x0000000000000001ULL,
    }
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
        0x0000000000000080ULL,
    }
};

const MoveDictionary knightMoveDictionary = {
    .size = 64,
    .boards = {
        0x0000000000020400ULL,
        0x0000000000050800ULL,
        0x00000000000a1100ULL,
        0x0000000000142200ULL,
        0x0000000000284400ULL,
        0x0000000000508800ULL,
        0x0000000000a01000ULL,
        0x0000000000402000ULL,
        0x0000000002040004ULL,
        0x0000000005080008ULL,
        0x000000000a110011ULL,
        0x0000000014220022ULL,
        0x0000000028440044ULL,
        0x0000000050880088ULL,
        0x00000000a0100010ULL,
        0x0000000040200020ULL,
        0x0000000204000402ULL,
        0x0000000508000805ULL,
        0x0000000a1100110aULL,
        0x0000001422002214ULL,
        0x0000002844004428ULL,
        0x0000005088008850ULL,
        0x000000a0100010a0ULL,
        0x0000004020002040ULL,
        0x0000020400040200ULL,
        0x0000050800080500ULL,
        0x00000a1100110a00ULL,
        0x0000142200221400ULL,
        0x0000284400442800ULL,
        0x0000508800885000ULL,
        0x0000a0100010a000ULL,
        0x0000402000204000ULL,
        0x0002040004020000ULL,
        0x0005080008050000ULL,
        0x000a1100110a0000ULL,
        0x0014220022140000ULL,
        0x0028440044280000ULL,
        0x0050880088500000ULL,
        0x00a0100010a00000ULL,
        0x0040200020400000ULL,
        0x0204000402000000ULL,
        0x0508000805000000ULL,
        0x0a1100110a000000ULL,
        0x1422002214000000ULL,
        0x2844004428000000ULL,
        0x5088008850000000ULL,
        0xa0100010a0000000ULL,
        0x4020002040000000ULL,
        0x0400040200000000ULL,
        0x0800080500000000ULL,
        0x1100110a00000000ULL,
        0x2200221400000000ULL,
        0x4400442800000000ULL,
        0x8800885000000000ULL,
        0x100010a000000000ULL,
        0x2000204000000000ULL,
        0x0004020000000000ULL,
        0x0008050000000000ULL,
        0x00110a0000000000ULL,
        0x0022140000000000ULL,
        0x0044280000000000ULL,
        0x0088500000000000ULL,
        0x0010a00000000000ULL,
        0x0020400000000000ULL
    }
};

const MoveDictionary kingMoveDictionary = {
    .size = 64,
    .boards = {
        0x0000000000000302,
        0x0000000000000705,
        0x0000000000000E0A,
        0x0000000000001C14,
        0x0000000000003828,
        0x0000000000007050,
        0x000000000000E0A0,
        0x000000000000C040,
        0x0000000000030203,
        0x0000000000070507,
        0x00000000000E0A0E,
        0x00000000001C141C,
        0x0000000000382838,
        0x0000000000705070,
        0x0000000000E0A0E0,
        0x0000000000C040C0,
        0x0000000003020300,
        0x0000000007050700,
        0x000000000E0A0E00,
        0x000000001C141C00,
        0x0000000038283800,
        0x0000000070507000,
        0x00000000E0A0E000,
        0x00000000C040C000,
        0x0000000302030000,
        0x0000000705070000,
        0x0000000E0A0E0000,
        0x0000001C141C0000,
        0x0000003828380000,
        0x0000007050700000,
        0x000000E0A0E00000,
        0x000000C040C00000,
        0x0000030203000000,
        0x0000070507000000,
        0x00000E0A0E000000,
        0x00001C141C000000,
        0x0000382838000000,
        0x0000705070000000,
        0x0000E0A0E0000000,
        0x0000C040C0000000,
        0x0003020300000000,
        0x0007050700000000,
        0x000E0A0E00000000,
        0x001C141C00000000,
        0x0038283800000000,
        0x0070507000000000,
        0x00E0A0E000000000,
        0x00C040C000000000,
        0x0302030000000000,
        0x0705070000000000,
        0x0E0A0E0000000000,
        0x1C141C0000000000,
        0x3828380000000000,
        0x7050700000000000,
        0xE0A0E00000000000,
        0xC040C00000000000,
        0x0203000000000000,
        0x0507000000000000,
        0x0A0E000000000000,
        0x141C000000000000,
        0x2838000000000000,
        0x5070000000000000,
        0xA0E0000000000000,
        0x40C0000000000000
    }
};

const SpecialMoveDictionary specialMoveDictionary = {
    .queenCastle = { 
        0x6000000000000000,
        0x0000000000000060
    },
    .kingCastle = {
        0x0600000000000000,
        0x0000000000000006
    },
    .queenCastlePositions = {
        {{8, 4}, {8, 3}},
        {{1, 4}, {1, 3}}
    },
    .kingCastlePositions = {
        {{8, 6}, {8, 7}},
        {{1, 6}, {1, 7}}
    },
    .pawn1SquareAdd = {-8, 8},
    .pawn2SquareAdd = {-16, 16},
    .pawnCaptureRight = {-9, 7},
    .pawnCaptureLeft = {-7, 9},
    .qCastleRookPositions = {
        {8, 1}, {1, 1}
    },
    .kCastleRookPositions = {
        {1, 8}, {8, 8}
    },
    .kCastleRookBitboard = {
        0x0500000000000000,
        0x0000000000000005
    },
    .qCastleRookBitboard = {
        0xA000000000000000,
        0x00000000000000A0
    },
};

const MoveDictionary positionDictionary = {
   .size = 64,
   .boards = {
        0x0000000000000001,
   	    0x0000000000000002,
   	    0x0000000000000004,
   	    0x0000000000000008,
   	    0x0000000000000010,
   	    0x0000000000000020,
   	    0x0000000000000040,
   	    0x0000000000000080,
   	    0x0000000000000100,
   	    0x0000000000000200,
   	    0x0000000000000400,
   	    0x0000000000000800,
   	    0x0000000000001000,
       	0x0000000000002000,
   	    0x0000000000004000,
   	    0x0000000000008000,
   	    0x0000000000010000,
   	    0x0000000000020000,
   	    0x0000000000040000,
   	    0x0000000000080000,
   	    0x0000000000100000,
   	    0x0000000000200000,
   	    0x0000000000400000,
   	    0x0000000000800000,
   	    0x0000000001000000,
   	    0x0000000002000000,
   	    0x0000000004000000,
   	    0x0000000008000000,
   	    0x0000000010000000,
        0x0000000020000000,
   	    0x0000000040000000,
   	    0x0000000080000000,
   	    0x0000000100000000,
   	    0x0000000200000000,
   	    0x0000000400000000,
   	    0x0000000800000000,
   	    0x0000001000000000,
   	    0x0000002000000000,
   	    0x0000004000000000,
   	    0x0000008000000000,
   	    0x0000010000000000,
   	    0x0000020000000000,
   	    0x0000040000000000,
   	    0x0000080000000000,
   	    0x0000100000000000,
   	    0x0000200000000000,
   	    0x0000400000000000,
   	    0x0000800000000000,
   	    0x0001000000000000,
   	    0x0002000000000000,
   	    0x0004000000000000,
   	    0x0008000000000000,
   	    0x0010000000000000,
   	    0x0020000000000000,
   	    0x0040000000000000,
   	    0x0080000000000000,
   	    0x0100000000000000,
   	    0x0200000000000000,
   	    0x0400000000000000,
   	    0x0800000000000000,
   	    0x1000000000000000,
   	    0x2000000000000000,
   	    0x4000000000000000,
   	    0x8000000000000000
   }
};

