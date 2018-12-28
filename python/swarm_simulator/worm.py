import random
import pygame
from color import Color


class Worm:
    def __init__(self, game):
        self.game = game

        self.worm_width = 5
        self.worm_height = 5

        self.dir_x = random.randrange(-1, 1)
        self.dir_y = random.randrange(-1, 1)
        self.speed = random.randrange(1, 5)
        self.x = random.randrange(0, game.display_width - self.worm_width)
        self.y = random.randrange(0, game.display_height - self.worm_height)

    def draw(self):
        game_display = self.game.game_display
        color = (255, 255, 255)
        pygame.draw.rect(game_display, color, [self.x, self.y, self.worm_width, self.worm_height])

    def update(self):
        if self.x <= 0 or self.x >= self.game.display_width - self.worm_width:
            self.dir_x *= -1
        if self.y <= 0 or self.y >= self.game.display_height - self.worm_height:
            self.dir_y *= -1
        elif random.random() < 0.005:
            self.dir_x = random.randrange(-1, 1)
            self.dir_y = random.randrange(-1, 1)
            self.speed = random.randrange(1, 5)

        self.x += self.dir_x * self.speed
        self.y += self.dir_y * self.speed
