<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" encoding="utf-8"/>

    <xsl:template match="/">
        
        <!-- Make sure that the xhtml page contains all the neccessarry headers! -->
        <xsl:text disable-output-escaping="yes"><![CDATA[
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <?xml version="1.0" encoding="UTF-8"?>]]>
        </xsl:text>
        
        <html xmlns="http://www.w3.org/1999/xhtml">
            
            <head>
                <title>On My Way</title>
                <link rel="stylesheet" href="style.css" />
            </head>
            
            <body>
            
                <div class="header">
                    <h1>On My Way</h1>
                    <h2>Malý, cestovně-statistický, deníček jednoho řidiče ...</h2>
                </div>

                <div class="navigation">
                    <ul>
                        <li class="menuItem"><a href="./index.html">Úvodní stránka</a></li>
                        <li class="menuItem"><a href="./aboutProject.html">O projektu</a></li>
                        <li class="menuItem"><a href="./aboutRoad.html">O cestě</a></li>
                        <li class="menuItem"><a href="./aboutDriver.html">O řidiči</a></li>
                        <li class="menuItemSelected"><a href="./data.xml">Statistika</a></li>
                        <li class="menuItem"><a href="./siteMap.html">Mapa stránek</a></li>
                    </ul>
                </div>

                <div class="body">
                    <xsl:apply-templates />
                </div>

            </body>

        </html>

    </xsl:template>
    
    <xsl:template match="StageDefinitions">
    
        <table summary="Tabulka prezentujicí časové údaje o jednitlivých úsecéch cesty"> 
            <caption>Časy rychlostních zkoušek</caption> 
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <thead></thead>
            <tbody></tbody> 
        </table>
    
    </xsl:template>
    
</xsl:stylesheet>
