import os
from argparse import ArgumentParser


class Field:
    LETTER = "L"
    BLOCK = "B"
    SECRET = "S"

    def __init__(self, type, x, y):
        self.type = type
        self.x = x
        self.y = y
        self.letter_horizontal = " "
        self.letter_vertical = " "
        self.word_length_horizontal = 0
        self.word_length_vertical = 0
        self.word_idx_horizontal = -1
        self.word_idx_vertical = -1
        self.word_validated_horizontal = False
        self.word_validated_vertical = False

    def __str__(self):
        if self.type == Field.LETTER:
            letter = self.letter_horizontal if self.letter_horizontal != " " else self.letter_vertical
            return " {} ".format(letter.replace("Х", "CH").ljust(2))
        elif self.type == Field.SECRET:
            letter = self.letter_horizontal if self.letter_horizontal != " " else self.letter_vertical
            return "({})".format(letter.replace("Х", "CH").ljust(2))
        else:
            return " ## "


    # def __str__(self):
    #     if self.type == Field.LETTER:
    #         return " {}{} ".format(self.word_length_horizontal, self.word_length_vertical)
    #     elif self.type == Field.SECRET:
    #         return "({}{})".format(self.word_length_horizontal, self.word_length_vertical)
    #     else:
    #         return " ## "


def parse_word_list(wl):
    words = {}
    word_list = wl.split(',')
    for word in word_list:
        word = word.replace("ch", "Х").upper()  # Cyrillic Х, not latin X
        if len(word) in words:
            words[len(word)].append({"word": word, "used": False})
        else:
            words[len(word)] = [{"word": word, "used": False}]
    return words


def parse_board(dx, dy, layout):
    board = []
    for y in range(dy):
        line = []
        for x in range(dx):
            field = Field(layout[y * dx + x], x, y)
            line.append(field)
        board.append(line)
    return board


def print_board(board):
    for line in board:
        for field in line:
            print("{}".format(str(field)), end="")
        print("")
    print("")


def print_debug(board, words, current_field, conflicting_field, backtracking):
    print("")
    for line in board:
        for field in line:
            print("(", end="")
            if field.word_length_vertical > 0:
                print("{},".format(field.word_idx_vertical + 1), end="")
            else:
                print(" ,", end="")
            if field.word_length_horizontal > 0:
                print("{}".format(field.word_idx_horizontal + 1), end="")
            else:
                print(" ", end="")
            print(")  ", end="")
        print("")
    print("")

    for word_length, word_list in words.items():
        print("{}: ".format(word_length), end="")
        for word in word_list:
            if not word["used"]:
                print("{}, ".format(word["word"]), end="")
        print("")
    print("")

    print("Backtracking: {}".format("Yes" if backtracking else "No"))

    print("Conflicting Field: ", end="")
    if conflicting_field is not None:
        print("({},{})".format(conflicting_field.x, conflicting_field.y))
    else:
        print("None")

    print("Current Field: ", end="")
    if current_field is not None:
        print("({},{})".format(current_field.x, current_field.y))
    else:
        print("None")
    print("")


def initialize_board(board, words):
    # initialize rows first
    for y in range(len(board)):
        find_line_words(board[y], words)

    for x in range(len(board[0])):
        find_column_words(x, board, words)


def find_line_words(line, words):
    x = 0
    while x < len(line):
        while x < len(line) and line[x].type == Field.BLOCK:
            x += 1

        if x < len(line):
            word_start = line[x]
            word_end = None
            while x < len(line) and line[x].type != Field.BLOCK:
                x += 1
            if x <= len(line):
                word_end = line[x - 1]

            if word_start is not None and word_end is not None:
                word_len = word_end.x - word_start.x + 1
                if word_len in words:
                    word_start.word_length_horizontal = word_len


def find_column_words(x, board, words):
    y = 0
    while y < len(board):
        while y < len(board) and board[y][x].type == Field.BLOCK:
            y += 1

        if y < len(board):
            word_start = board[y][x]
            word_end = None
            while y < len(board) and board[y][x].type != Field.BLOCK:
                y += 1
            if y <= len(board):
                word_end = board[y-1][x]

            if word_start is not None and word_end is not None:
                word_len = word_end.y - word_start.y + 1
                if word_len in words:
                    word_start.word_length_vertical = word_len


def remove_word_horizontal(board, start_field, candidates):
    for x in range(start_field.x, start_field.x + start_field.word_length_horizontal):
        board[start_field.y][x].letter_horizontal = " "
    candidates[start_field.word_idx_horizontal]["used"] = False


def remove_word_vertical(board, start_field, candidates):
    for y in range(start_field.y, start_field.y + start_field.word_length_vertical):
        board[y][start_field.x].letter_vertical = " "
    candidates[start_field.word_idx_vertical]["used"] = False


def advance(x, y, max_x, backtracking):
    if backtracking:
        if x - 1 == -1:
            x = max_x - 1
            y -= 1
        else:
            x -= 1
    else:
        if x + 1 == max_x:
            x = 0
            y += 1
        else:
            x += 1
    return x, y


def validate_word_horizontal(start_field, word, board):
    if word["used"]:
        return False, None

    word_idx = 0
    for x in range(start_field.x, start_field.x + start_field.word_length_horizontal):
        field = board[start_field.y][x]
        if field.letter_vertical != " " and field.letter_vertical != word["word"][word_idx]:
            conflicting_field = field
            while conflicting_field.word_idx_vertical == -1:
                conflicting_field = board[conflicting_field.y - 1][x]
            return False, conflicting_field
        word_idx += 1
    return True, None


def add_word_horizontal(start_field, candidates, board):
    word = candidates[start_field.word_idx_horizontal]
    valid, conflicting_field = validate_word_horizontal(start_field, word, board)
    if not valid:
        return valid, conflicting_field

    word_idx = 0
    for x in range(start_field.x, start_field.x + start_field.word_length_horizontal):
        board[start_field.y][x].letter_horizontal = word["word"][word_idx]
        word_idx += 1

    word["used"] = True

    return True, None


def validate_word_vertical(start_field, word, board):
    if word["used"]:
        return False, None

    word_idx = 0
    for y in range(start_field.y, start_field.y + start_field.word_length_vertical):
        field = board[y][start_field.x]
        if field.letter_horizontal != " " and field.letter_horizontal != word["word"][word_idx]:
            conflicting_field = field
            while conflicting_field.word_idx_horizontal == -1:
                conflicting_field = board[y][conflicting_field.x - 1]
            return False, conflicting_field
        word_idx += 1
    return True, None


def add_word_vertical(start_field, candidates, board):
    word = candidates[start_field.word_idx_vertical]
    valid, conflicting_field = validate_word_vertical(start_field, word, board)
    if not valid:
        return valid, conflicting_field

    word_idx = 0
    for y in range(start_field.y, start_field.y + start_field.word_length_vertical):
        board[y][start_field.x].letter_vertical = word["word"][word_idx]
        word_idx += 1

    word["used"] = True

    return True, None


def solve_board(board, words):
    x = 0
    y = 0
    len_x = len(board[0])
    len_y = len(board)
    backtracking = False
    conflicting_field = None

    while 0 <= y < len_y and 0 <= x < len_x:
        field = board[y][x]

        # skip fields where there is not start of a word
        if field.word_length_vertical == 0 and field.word_length_horizontal == 0:
            x, y = advance(x, y, len_x, backtracking)
            continue

        # os.system("clear")
        # print_board(board)
        # print_debug(board, words, field, conflicting_field, backtracking)
        # input("Press Enter to continue...")

        if backtracking:
            if field.word_length_horizontal > 0:
                candidates = words[field.word_length_horizontal]
                remove_word_horizontal(board, field, candidates)
                field.word_validated_horizontal = False
            if field.word_length_vertical > 0:
                candidates = words[field.word_length_vertical]
                remove_word_vertical(board, field, candidates)
                field.word_validated_vertical = False
            if conflicting_field is not None:
                # we know which field we had a most recent conflict with, we need to backtrack all the way there
                # and invalidate everything along the way
                if field.x != conflicting_field.x or field.y != conflicting_field.y:
                    field.word_idx_vertical = -1
                    field.word_validated_vertical = False
                    field.word_idx_horizontal = -1
                    field.word_validated_horizontal = False
                    x, y = advance(x, y, len_x, backtracking)
                    continue


        # we have a field where there is a start of a word in a line
        if field.word_length_horizontal > 0 and not field.word_validated_horizontal:
            candidates = words[field.word_length_horizontal]
            # we have run out of options - we have to backtrack
            if field.word_idx_horizontal == len(candidates) - 1:
                field.word_idx_horizontal = -1
                backtracking = True
                x, y = advance(x, y, len_x, backtracking)
            else:
                backtracking = False
                field.word_idx_horizontal += 1
                success, cf = add_word_horizontal(field, candidates, board)
                if success:
                    field.word_validated_horizontal = True
                    field.word_idx_vertical = -1
                    field.word_validated_vertical = False
                    conflicting_field = cf # should be None
                else:
                    if cf is not None:
                        if conflicting_field is not None:
                            if cf.x > conflicting_field.x:
                                conflicting_field = cf
                        else:
                            conflicting_field = cf

        # we have a field where there is a start of a word in a column
        elif field.word_length_vertical > 0 and not field.word_validated_vertical:
            candidates = words[field.word_length_vertical]
            # we have run out of options - we have to backtrack
            if field.word_idx_vertical == len(candidates) - 1:
                field.word_idx_vertical = -1
                if field.word_length_horizontal > 0:
                    field.word_validated_horizontal = False
                else:
                    backtracking = True
                    x, y = advance(x, y, len_x, backtracking)
            else:
                backtracking = False
                field.word_idx_vertical += 1
                success, cf = add_word_vertical(field, candidates, board)
                if success:
                    field.word_validated_vertical = True
                    conflicting_field = cf  # should be None
                else:
                    if cf is not None:
                        if conflicting_field is not None:
                            if cf.y > conflicting_field.y:
                                conflicting_field = cf
                        else:
                            conflicting_field = cf

        else:
            os.system("clear")
            print_board(board)
            x, y = advance(x, y, len_x, backtracking)

    os.system("clear")


def main():
    parser = ArgumentParser()
    parser.add_argument("-dx", "--dimension_x", dest="dx",
                        help="X-dimension of the board", type=int)
    parser.add_argument("-dy", "--dimension_y", dest="dy",
                        help="y-dimension of the board", type=int)
    parser.add_argument("-l", "--layout", dest="layout",
                        help="Layout of the board from left to right, from top to bottom. "
                             "L=letter, B=block, S=secret", type=str)
    parser.add_argument("-wl", "--wordlist", dest="wl",
                        help="Comma-separated list of words to place on the board")

    args = parser.parse_args()

    words = parse_word_list(args.wl)
    board = parse_board(args.dx, args.dy, args.layout)
    initialize_board(board, words)
    solve_board(board, words)
    print_board(board)


if __name__ == "__main__":
    main()
