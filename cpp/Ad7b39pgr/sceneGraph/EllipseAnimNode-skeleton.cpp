#include "pgr.h"   // includes all PGR libraries, like shader, glm, assimp ...
#include "EllipseAnimNode.h"


#ifndef M_PI
#define M_PI 3.14159f
#endif

#define DEG_TO_RAD (M_PI/180.0f)
#define RAD_TO_DEG (180.0f/M_PI)

EllipseAnimNode::EllipseAnimNode(const char* name, SceneNode* parent):
SceneNode(name, parent), m_axis(1, 0, 0), m_angle(0), m_speed(0)
{
}

void EllipseAnimNode::update(double elapsed_time)
{
    m_angle = m_speed * (float) elapsed_time;

    // =============================== BEGIN OF SOLUTION - TASK 3 ====================================
    m_local_mat = glm::mat4(1.0f);
    m_local_mat = glm::translate(m_local_mat, glm::vec3(m_majorAxis * cos(m_angle), 0.0f, -m_minorAxis * sin(m_angle)));
    m_local_mat = glm::rotate(m_local_mat, glm::degrees(m_angle), glm::vec3(0.0f, 1.0f, 0.0f));
    // =============================== END OF SOLUTION - TASK 3 ======================================

    /// call inherited update (which calculates global matrix and updates children)
    SceneNode::update(elapsed_time);
}

