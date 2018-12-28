import pygame

from worm import Worm
from color import Color


class SwarmSimulator:
    def __init__(self):
        pygame.init()

        self.display_width = 800
        self.display_height = 600
        self.game_display = pygame.display.set_mode((self.display_width, self.display_height))
        self.diag_font = pygame.font.SysFont(None, 25)
        self.small_font = pygame.font.SysFont("comicsansms", 25)
        self.medium_font = pygame.font.SysFont("comicsansms", 50)
        self.large_font = pygame.font.SysFont("comicsansms", 80)

        self.swarm = self.init_swarm()

    def message_to_screen(self, msg, color, y_displace=0, size="small"):
        font = self.small_font
        if size == "medium":
            font = self.medium_font
        elif size == "large":
            font = self.large_font

        screen_text = font.render(msg, True, color)
        text_rect = screen_text.get_rect()
        text_rect.center = [self.display_width / 2, self.display_height / 2 + y_displace]
        self.game_display.blit(screen_text, text_rect)

    def update(self):
        for worm in self.swarm:
            worm.update()

    def draw(self):
        self.game_display.fill(Color.BLACK)

        for worm in self.swarm:
            worm.draw()

        pygame.display.flip()

    def run(self):
        clock = pygame.time.Clock()
        fps = 100

        game_exit = False

        while not game_exit:

            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    game_exit = True

            self.update()
            self.draw()

            clock.tick(fps)

        pygame.quit()
        quit()

    def init_swarm(self):
        worms = []
        for i in range(0, 1000):
            worms.append(Worm(self))

        return worms
