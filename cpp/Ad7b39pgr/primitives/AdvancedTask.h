#pragma once
#include "taskbase.h"

class AdvancedTask : public TaskBase
{
public:
    AdvancedTask(void);

    virtual void InitializeProgram();

protected:
    virtual void ConfigureShaders(ShaderVectorPtr shaders);

    GLint m_iColorLocation;

private:
    std::string m_strVertexShader;
    std::string m_strFragmentShader;
};
