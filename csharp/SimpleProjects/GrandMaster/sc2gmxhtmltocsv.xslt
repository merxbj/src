<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xh="http://www.w3.org/1999/xhtml">

    <xsl:output method="text" indent="false"/>
    <xsl:strip-space/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="/xh:html/xh:body/xh:div[@id='wrapper']/xh:div[@id='content']/xh:div[@id='content-top']/xh:div[@id='content-bot']/xh:div[@class='ladder']/xh:div[@id='ladder']/xh:table/xh:tbody/xh:tr"/>
    </xsl:template>

    <xsl:template match="/xh:html/xh:body/xh:div[@id='wrapper']/xh:div[@id='content']/xh:div[@id='content-top']/xh:div[@id='content-bot']/xh:div[@class='ladder']/xh:div[@id='ladder']/xh:table/xh:tbody/xh:tr">
        <xsl:value-of select="xh:td[last()-5]/@data-raw"/>,<xsl:value-of select="xh:td[last()-4]/xh:a"/>,<xsl:value-of select="xh:td[last()]"/>,<xsl:value-of select="substring-after(xh:td[last()-6]/@data-tooltip, 'Joined Division: ')"/>,<xsl:value-of select="xh:td[last()-3]"/>,<xsl:value-of select="xh:td[last()-2]"/>,<xsl:value-of select="xh:td[last()-1]"/>,
    </xsl:template>

</xsl:stylesheet>
