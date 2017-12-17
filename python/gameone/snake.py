import pygame, random

pygame.init()

black, white, red, green, blue = (0, 0, 0), (255, 255, 255), (255, 0, 0), (0, 255, 0), (0, 0, 255)
to_left, to_right, up, down = pygame.math.Vector2(-1, 0), pygame.math.Vector2(1, 0), pygame.math.Vector2(0,
                                                                                                         -1), pygame.math.Vector2(
    0, 1)
stand = pygame.math.Vector2(0, 0)
block_size = 10
apple_size = 10
display_width, display_height = 800, 600

clock = pygame.time.Clock()

gameDisplay = pygame.display.set_mode((display_width, display_height))
pygame.display.set_caption('Snake')
font = pygame.font.SysFont(None, 25)


def draw_snake(snake):
    for block in snake:
        pygame.draw.rect(gameDisplay, white, [block.x, block.y, block_size, block_size])


def message_to_screen(msg, color):
    screen_text = font.render(msg, True, color)
    gameDisplay.blit(screen_text, [display_width / 2, display_height / 2])


def get_random_pos(object_size):
    x = random.randrange(0, display_width - object_size)
    y = random.randrange(0, display_height - object_size)
    return pygame.math.Vector2(round(x / 10.0) * 10.0, round(y / 10.0) * 10.0)


def stats_to_screen(snake_speed, snake_length, lead_pos):
    speed_text = font.render('Length = {}, Speed = {}, PosX = {}, PosY = {}'.format(snake_length, snake_speed, lead_pos.x, lead_pos.y), True, red)
    gameDisplay.blit(speed_text, [5, 5])


def gameLoop():
    speed = 10
    direction = to_left
    snake_length = 1
    lead_pos = pygame.math.Vector2(display_width / 2, display_height / 2)
    snake = [pygame.math.Vector2(lead_pos)]
    apple_pos = get_random_pos(apple_size)
    FPS = 15

    gameOver = False
    gameExit = False

    while not gameExit:

        while gameOver:
            gameDisplay.fill(black)
            message_to_screen("Game over, press C to play again or Q to exit.", red)
            pygame.display.flip()

            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    gameExit = True
                    gameOver = False
                if event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_q:
                        gameExit = True
                        gameOver = False
                    if event.key == pygame.K_c:
                        gameExit = False
                        gameOver = False
                        direction = to_left
                        lead_pos = pygame.math.Vector2(display_width / 2, display_height / 2)
                        apple_pos = get_random_pos(apple_size)

            clock.tick(FPS)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                gameExit = True
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT or event.key == pygame.K_a:
                    if direction != to_right:
                        direction = to_left
                elif event.key == pygame.K_RIGHT or event.key == pygame.K_d:
                    if direction != to_left:
                        direction = to_right
                elif event.key == pygame.K_UP or event.key == pygame.K_w:
                    if direction != down:
                        direction = up
                elif event.key == pygame.K_DOWN or event.key == pygame.K_s:
                    if direction != up:
                        direction = down
                elif event.key == pygame.K_r:
                    gameOver = False
                    direction = to_left
                    lead_pos = pygame.math.Vector2(display_width / 2, display_height / 2)

        lead_pos += (direction * speed)

        if lead_pos.x <= 0 or lead_pos.x + block_size >= 800 or lead_pos.y <= 0 or lead_pos.y + block_size >= 600:
            gameOver = True

        for block in snake:
            if block.x == lead_pos.x and block.y == lead_pos.y:
                gameOver = True

        if lead_pos.x >= apple_pos.x and lead_pos.x + block_size <= apple_pos.x + apple_size:
            if lead_pos.y >= apple_pos.y and lead_pos.y + block_size <= apple_pos.y + apple_size:
                apple_pos = get_random_pos(apple_size)
                snake_length += 1
                FPS += 5

        snake.append(pygame.math.Vector2(lead_pos))
        if len(snake) > snake_length:
            del snake[0]

        gameDisplay.fill(black)

        pygame.draw.rect(gameDisplay, green, [apple_pos.x, apple_pos.y, apple_size, apple_size])
        draw_snake(snake)
        stats_to_screen(FPS, snake_length, lead_pos)
        pygame.display.flip()

        clock.tick(FPS)

    pygame.quit()
    quit()


gameLoop()
