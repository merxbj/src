'Zadani:
'Vytvorte makro ve VBA, ktere vykresli ctverec postaveny na spicku (s vodorovnou uhloprickou) s kruznici opsanou a vepsanou, podle nasledujicich pokynu (chcete-li pouzit jine vyvojove prostredi, jako napr. ObjectARX a C++, nebo C#, muzete).
'1. Makro po spusteni vyzve uzivatele pro zadani bodu, ktery bude reprezentovat pravy bod pro ctverec postaveny na spicku (s vodorovnou uhloprickou). Zadani bodu se bude chovat standardne, napr. jako standardni prikaz AutoCADu pro kruznici pri zadavani jejiho stredu.
'2. Po zadani prvniho bodu vyzve k zadani dalsiho udaje, kterym je polomer opsane kruznice. (I zde je preferovane reseni, ktere se bude chovat standardne jako u podobnych prikazu v AutoCADu - v dusledku je mozne rozmer urcit zadanim druheho bodu nebo napr. primo cislem. Pokud toto reseni nezvladnete, pokuste se o alternativni jednodussi reseni. Nejhorsi pripad je pevne zadani souradnic v kodu makra. Kazde zjednoduseni vsak bude mit vliv na bodove hodnoceni zadanni.)
'3. Makro vykresli ctverec postaveny na spicku (s vodorovnou uhloprickou) a do nej kruznici vepsanou a kolem nej kruznici opsanou. (Nezadane body a rozmery dopocte.)
'4. Makro tvary umisti do hladin "ctverec" (fialova barva), "kruznice vepsana" (svetle modra barva) a "kruznice opsana" (tmave modra barva). Makro si hladiny vytvari samo, pokud neexistuji, a nastavi jim barvy uvedene v zavorkach.
'5. Makro na zaver zobrazi MessageBox (okno) s informaci "Obsah ctverce je ....", doplneno vypoctenym cislem.
'6. Makro (pokud je ve VBA, C# nebo jinem jazyce podporujicim vlastnosti) bude mit a korektne vyuzivat vlastnosti pro polomer opsane kruznice a Obsah ctverce.

Dim radius As Double
Property Get RadiusOfExcircle() As Double
    RadiusOfExcircle = radius
End Property

Property Let RadiusOfExcircle(newRadius As Double)
    radius = newRadius
End Property

Property Get RadiusOfIncircle() As Double
    RadiusOfIncircle = RadiusOfExcircle / Sqr(2)
End Property

Property Get AreaOfSquare() As Double
    AreaOfSquare = RadiusOfIncircle ^ 2
End Property

Function distance(point As Variant, otherPoint As Variant)
    Let x1 = point(0)
    Let y1 = point(1)
    Let x2 = otherPoint(0)
    Let y2 = otherPoint(1)
    distance = Sqr((x2 - x1) ^ 2 + (y2 - y1) ^ 2)
End Function

Sub createAndSwithToLayerWithColor(layerName As String, layerColor As AcColor)
    
    ' Create the new layer, if not exists
    Dim newLayer As AcadLayer
    On Error Resume Next
    Set newLayer = ThisDrawing.Layers(layerName)
    If newLayer Is Nothing Then
        Set newLayer = ThisDrawing.Layers.Add(layerName)
        If newLayer Is Nothing Then
            MsgBox "Failed to create a new layer: " & layerName
            Exit Sub
        End If
    End If
    
    ' Set the layer color
    Dim newColor As AcadAcCmColor
    Set newColor = AcadApplication.GetInterfaceObject("AutoCAD.AcCmColor.18")

    newColor.ColorMethod = acColorMethodByRGB
    newColor.ColorIndex = layerColor
    newLayer.TrueColor = newColor
    newColor = Nothing

    ' Switch the active layer
    ThisDrawing.ActiveLayer = newLayer
End Sub

Sub drawVertexStandingSquare(rightVertex As Variant)

    ' At first calculate the vertices of the square
    Dim vertices(0 To 7) As Double
    vertices(0) = rightVertex(0): vertices(1) = rightVertex(1)                                          ' Right vertex is easy
    vertices(2) = rightVertex(0) - RadiusOfExcircle: vertices(3) = rightVertex(1) + RadiusOfExcircle    ' Top vertex
    vertices(4) = rightVertex(0) - 2 * RadiusOfExcircle: vertices(5) = rightVertex(1)                   ' Left vertex
    vertices(6) = rightVertex(0) - RadiusOfExcircle: vertices(7) = rightVertex(1) - RadiusOfExcircle    ' Bottom vertex

    ' Now draw the poly line
    Dim vertexStandingSquare As AcadLWPolyline
    Set vertexStandingSquare = ThisDrawing.ModelSpace.AddLightWeightPolyline(vertices)
    
    ' And finally close it to get the square
    vertexStandingSquare.Closed = True
    vertexStandingSquare.Update

End Sub

Sub drawHorizontalDiagonal(rightVertex As Variant)

    Dim leftVertex(0 To 2) As Double
    leftVertex(0) = rightVertex(0) - 2 * RadiusOfExcircle: leftVertex(1) = rightVertex(1): leftVertex(2) = 0
    ThisDrawing.ModelSpace.AddLine leftVertex, rightVertex

End Sub

Sub drawCircle(radius As Double, centerPoint As Variant)
    ThisDrawing.ModelSpace.AddCircle centerPoint, radius
End Sub

Sub drawSquareWithAllThoseCirclesMain()

    ' Get the right vertex of the square
    Dim rightVertex As Variant
    rightVertex = ThisDrawing.Utility.GetPoint(, "Pravy roh: ")
    
    ' Get the radius of the excircle by getting relative point to the right vertex
    Dim otherPoint As Variant
    otherPoint = ThisDrawing.Utility.GetPoint(rightVertex, "Polomer opsane kruznice: ")
    
    ' Calculate the distance between the right vertex and the other point (= radius of excircle)
    RadiusOfExcircle = distance(rightVertex, otherPoint)
    
    ' Create 'ctverec' layer, switch to it and draw the square and the horizontal line in it
    createAndSwithToLayerWithColor "ctverec", acMagenta
    drawVertexStandingSquare (rightVertex)
    drawHorizontalDiagonal (rightVertex)
    
    ' Calculate the center for ex and in circle
    Dim centerPoint(0 To 2) As Double
    centerPoint(0) = rightVertex(0) - RadiusOfExcircle
    centerPoint(1) = rightVertex(1)
    centerPoint(2) = 0 ' Don't care about the Z-axis, but is's required
    
    ' Create 'kruzine opsana' layer, switch to it and draw the excircle
    createAndSwithToLayerWithColor "kruznice opsana", acBlue
    drawCircle RadiusOfExcircle, centerPoint
    
    ' Create 'kruzine opsana' layer, switch to it and draw the incircle
    createAndSwithToLayerWithColor "kruznice vepsana", acCyan
    drawCircle RadiusOfIncircle, centerPoint

    ' Show the areay of the square
    MsgBox "Obsah ctverce je " & AreaOfSquare
    
End Sub

