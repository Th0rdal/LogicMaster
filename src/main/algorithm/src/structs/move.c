#include "structs/move.h"

MoveFlags moveFlagsInit();

Move moveInit() {
    Move move;
    move.piece = NO_PIECE;
    move.startPosition = positionInit();
    move.endPosition = positionInit();
    move.flags = moveFlagsInit(); 

    return move;
}

MoveFlags moveFlagsInit() {
    MoveFlags flags = {
        .capture = false,
        .promotion = false,
        .check = false,
        .enPassant = false,
        .checkmate = false,
        .draw = false,
        .kCastle = {false, false},
        .qCastle = {false, false},
        .isWhiteTurn = false,
    };
    return flags;
}

void printMove(Move* move, char* string) {
    char moveString[20];
    strcpy(moveString, "");
    short counter = 0;

    if (move->flags.draw) {
        moveString[counter] = '1';
        counter++;
        moveString[counter] = '/';
        counter++;
        moveString[counter] = '2';
        counter++;
        moveString[counter] = '-';
        counter++;
        moveString[counter] = '1';
        counter++;
        moveString[counter] = '/';
        counter++;
        moveString[counter] = '2';
        counter++;
    } else if (move->flags.kCastle[move->flags.isWhiteTurn]) {
        moveString[counter] = 'O';
        counter++;
        moveString[counter] = '-';
        counter++;
        moveString[counter] = 'O';
        counter++;
    } else if (move->flags.qCastle[move->flags.isWhiteTurn]) {
        moveString[counter] = 'O';
        counter++;
        moveString[counter] = '-';
        counter++;
        moveString[counter] = 'O';
        counter++;
        moveString[counter] = '-';
        counter++;
        moveString[counter] = 'O';
        counter++;
    } else {

        if (!move->flags.promotion) {
            switch (move->piece) {
                case PAWN:
                    break;
                case ROOK:
                    moveString[counter] = 'R';
                    counter++;
                    break;
                case KNIGHT:
                    moveString[counter] = 'N';
                    counter++;
                    break;
                case BISHOP:
                    moveString[counter] = 'B';
                    counter++;
                    break;
                case QUEEN:
                    moveString[counter] = 'Q';
                    counter++;
                    break;
                case KING:
                    moveString[counter] = 'K';
                    counter++;
                    break;
                default:
                    break;
            }    
        }
        
    }
    
    moveString[counter] = move->startPosition.file + 'a' - 1;
    counter++;
    moveString[counter] = move->startPosition.rank + '0';
    counter++;

    if (move->flags.capture) {
        moveString[counter] = 'x';
        counter++;
    } else {
        moveString[counter] = ' ';
        counter++;
    }
    moveString[counter] = move->endPosition.file + 'a' - 1;
    counter++;
    moveString[counter] = move->endPosition.rank + '0';


    if (move->flags.promotion) {
        moveString[counter] = '=';
        counter++;
        switch (move->piece) {
            case ROOK:
                moveString[counter] = 'R';
                counter++;
                break;
            case KNIGHT:
                moveString[counter] = 'N';
                counter++;
                break;
            case BISHOP:
                moveString[counter] = 'B';
                counter++;
                break;
            case QUEEN:
                moveString[counter] = 'Q';
                counter++;
                break;
            case KING:
                moveString[counter] = 'K';
                counter++;
                break;
            default:
                break;
        } 
    } else if (move->flags.enPassant) {
        moveString[counter] = 'e';
        counter++;
        moveString[counter] = 'p';
        counter++;
    }

    if (move->flags.checkmate) {
        moveString[counter] = '#';
        counter++;
    } else if (move->flags.check) {
        moveString[counter] = '+';
        counter++;
    }
    strcpy(string, moveString);
}
