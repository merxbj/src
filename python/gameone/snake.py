import pygame, random

pygame.init()

black, white, red, green, blue = (0, 0, 0), (255, 255, 255), (255, 0, 0), (0, 255, 0), (0, 0, 255)
to_left, to_right, up, down = pygame.math.Vector2(-1, 0), pygame.math.Vector2(1, 0), pygame.math.Vector2(0,
                                                                                                         -1), pygame.math.Vector2(
    0, 1)
stand = pygame.math.Vector2(0, 0)
block_size = 20
apple_size = 20
display_width, display_height = 800, 600
head_img = pygame.image.load('res/head.png')
body_img = pygame.image.load('res/body.png')
apple_img = pygame.image.load('res/apple.png')

clock = pygame.time.Clock()

gameDisplay = pygame.display.set_mode((display_width, display_height))
pygame.display.set_caption('Snake')
diag_font = pygame.font.SysFont(None, 25)
small_font = pygame.font.SysFont("comicsansms", 25)
medium_font = pygame.font.SysFont("comicsansms", 50)
large_font = pygame.font.SysFont("comicsansms", 80)


def draw_snake(snake, direction):
    angle = direction.angle_to(pygame.math.Vector2(0,-1))
    gameDisplay.blit(pygame.transform.rotate(head_img, angle), (snake[-1].x, snake[-1].y))
    #pygame.draw.rect(gameDisplay, white, [snake[-1].x, snake[-1].y, block_size, block_size], 1)

    for block in snake[:-1]:
        gameDisplay.blit(body_img, (block.x, block.y))
        #pygame.draw.rect(gameDisplay, white, [block.x, block.y, block_size, block_size], 1)


def draw_apple(apple):
    gameDisplay.blit(apple_img, (apple.x, apple.y))


def message_to_screen(msg, color, y_displace=0, size="small"):
    font = small_font
    if size == "medium":
        font = medium_font
    elif size == "large":
        font = large_font

    screen_text = font.render(msg, True, color)
    text_rect = screen_text.get_rect()
    text_rect.center = [display_width / 2, display_height / 2 + y_displace]
    gameDisplay.blit(screen_text, text_rect)


def get_random_pos(object_size):
    x = random.randrange(0, display_width - object_size)
    y = random.randrange(0, display_height - object_size)
    return pygame.math.Vector2(round(x / 10.0) * 10.0, round(y / 10.0) * 10.0)


def stats_to_screen(snake_speed, snake_length, lead_pos):
    speed_text = diag_font.render('Length = {}, Speed = {}, PosX = {}, PosY = {}'.format(snake_length, snake_speed, lead_pos.x, lead_pos.y), True, red)
    gameDisplay.blit(speed_text, [5, 5])


def gameLoop():
    speed = block_size
    direction = to_left
    snake_length = 1
    lead_pos = pygame.math.Vector2(display_width / 2, display_height / 2)
    snake = [pygame.math.Vector2(lead_pos)]
    apple_pos = get_random_pos(apple_size)
    fps = 10

    game_over = False
    game_exit = False

    while not game_exit:

        while game_over:
            gameDisplay.fill(black)
            message_to_screen("Game over!", red, -50, "large")
            message_to_screen("Press C to play again or Q to exit.", white)
            pygame.display.flip()

            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    game_exit = True
                    game_over = False
                if event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_q:
                        game_exit = True
                        game_over = False
                    if event.key == pygame.K_c:
                        game_exit = False
                        game_over = False
                        direction = to_left
                        lead_pos = pygame.math.Vector2(display_width / 2, display_height / 2)
                        apple_pos = get_random_pos(apple_size)

            clock.tick(fps)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                game_exit = True
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
                    game_over = False
                    direction = to_left
                    lead_pos = pygame.math.Vector2(display_width / 2, display_height / 2)

        lead_pos += (direction * speed)

        if lead_pos.x <= 0 or lead_pos.x + block_size >= 800 or lead_pos.y <= 0 or lead_pos.y + block_size >= 600:
            game_over = True

        for block in snake:
            if block.x == lead_pos.x and block.y == lead_pos.y:
                game_over = True

        if lead_pos.x + block_size > apple_pos.x and lead_pos.x < apple_pos.x + apple_size:
            if lead_pos.y + block_size > apple_pos.y and lead_pos.y < apple_pos.y + apple_size:
                apple_pos = get_random_pos(apple_size)
                snake_length += 1


        snake.append(pygame.math.Vector2(lead_pos))
        if len(snake) > snake_length:
            del snake[0]

        gameDisplay.fill(black)

        draw_apple(apple_pos)
        draw_snake(snake, direction)
        stats_to_screen(fps, snake_length, lead_pos)
        pygame.display.flip()

        clock.tick(fps)

    pygame.quit()
    quit()


gameLoop()
