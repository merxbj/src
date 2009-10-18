#include "stdafx.h"
#include "GameEngine.h"

GameEngine* GameEngine::gameEngine = NULL;

GameEngine::GameEngine()
{
	this->mainWindow = NULL;
}

GameEngine* GameEngine::GetInstance()
{
	if (GameEngine::gameEngine == NULL)
		GameEngine::gameEngine = new GameEngine();

	return GameEngine::gameEngine;
}

void GameEngine::SetMainWindow(HWND hWnd)
{
	this->mainWindow = hWnd;
}

void GameEngine::Run()
{

}