<?xml version="1.0"?>

<!--
    Document   : quickfix.xsl
    Created on : 13. srpen 2011, 20:41
    Author     : eTeR
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml"/>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="/CluedoGame/Turns/Turn/Solution">
        <xsl:element name="Solution">
            <xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
            <xsl:if test="./Character/@name">
                <xsl:attribute name="character"><xsl:value-of select="Character/@name"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./Room/@name">
                <xsl:attribute name="room"><xsl:value-of select="Room/@name"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./Weapon/@name">
                <xsl:attribute name="weapon"><xsl:value-of select="Weapon/@name"/></xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="/CluedoGame/Turns/Turn/Answer">
        <xsl:element name="Answer">
            <xsl:attribute name="player"><xsl:value-of select="@player"/></xsl:attribute>
            <xsl:choose>
                <xsl:when test="./Character/@name">
                    <xsl:attribute name="character"><xsl:value-of select="Character/@name"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="./Room/@name">
                    <xsl:attribute name="room"><xsl:value-of select="Room/@name"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="./Weapon/@name">
                    <xsl:attribute name="weapon"><xsl:value-of select="Weapon/@name"/></xsl:attribute>
                </xsl:when>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
