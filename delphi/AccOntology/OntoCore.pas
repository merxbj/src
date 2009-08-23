unit OntoCore;

interface

uses
  xmldom, XMLIntf, msxmldom, XMLDoc, sysutils, Classes, StrUtils, OntoInstances,
  OntoUtils;

type

  TLocation = record
    IsInstance: boolean;
    Levels: TStringList;
    Name: string;
  end;

  PLocation = ^TLocation;

  PToFunction = function(CheckedNode: IXMLNode; Data: Pointer) : boolean of object;

  TOntoCore = class
    public
      // Pristupove funkce
      function GetClassLeafs(const Ontology: IXMLDocument; const ClassName: string; Leafs: PStringList) : integer;
      function GetClassInstances(const Ontology: IXMLDocument; const Element: string; Instances: PInstances) : integer;
      function AddDeviceToGenericOntology(GenericOntology: IXMLDocument; Device: PDevice; Location: TLocation; Ontology: IXMLDocument) : string;
      function AddDeviceToOntology(Ontology: IXMLDocument; Device: PDevice; Location: TLocation) : boolean;
      function RemoveDeviceFromOntology(Ontology: IXMLDocument; DeviceInstance: PInstance) : boolean;

    private
      // Implementace vyhledavacich funkci
      function FindClassLeafsRecursively(const NodeList: IXMLNodeList; ClassName: string; Leafs: PStringList) : integer;
      function FindClassInNode(const Node: IXMLNode; const Element: string) : string;
      function FindInstancesOf(const NodeList: IXmlNodeList; const ClassName: string; Instances: PInstances) : integer;

      // Implementace pridavacich funkci
      function WriteDeviceImplementation(ImplementationLocation: IXMLNode; Device: PDevice; Location: PLocation) : boolean;
      function WriteDeviceImplementationGeneric(ImplementationLocation: IXMLNode; Device: PDevice; Location: PLocation) : boolean;

      // Silna funkce :)
      function FindSpecificNode(ExploredNodes: IXMLNodeList; Criterium: PToFunction;  Data: Pointer; var FoundNode: IXMLNode; StartOver: boolean = true) : boolean;

      // Criterium funkce
      function IsImplementationLocation(CheckedNode: IXMLNode; Data: Pointer) : boolean;
      function IsChildOf(CheckedNode: IXMLNode; Data: Pointer) : boolean;
      function IsClassDefinitionOrReference(CheckedNode: IXMLNode; Data: Pointer) : boolean;
      function IsObjectDefinitionOrReference(CheckedNode: IXMLNode; Data: Pointer) : boolean;
      function IsObjectDefinition(CheckedNode: IXMLNode; Data: Pointer) : boolean;

      // Pomocne funkce
      function SaveOntologyElsewhere(Ontology: IXMLDocument) : string;
      function GetAdditionalLocationInfo(Location: PLocation; Ontology: IXMLDocument) : boolean;
      function VerifyGenericPartOfOntology(Ontology: IXMLDocument; Device: PDevice; Location: PLocation) : boolean;
      function AddFunctionsToDeviceGeneric(TargetNode: IXMLNode; Device: PDevice) : boolean;
      function AddPropertiesToDeviceGeneric(TargetNode: IXMLNode; Device: PDevice) : boolean;
      function AddNewClass(Ontology: IXMLDocument; ClassName: string; SubClassOf: PStringList) : boolean;
      function VerifyGenericDefinitionOfFunctions(Ontology: IXMLDocument; Device: PDevice) : boolean;
      function VerifyGenericDefinitionOfProperties(Ontology: IXMLDocument; Device: PDevice) : boolean;
      function AddDisjoints(Ontology: IXMLDocument; OfClass: string; OnSubclassOf: string; NewClassNode: IXMLNode) : boolean;
      function GetNextInstanceOf(TargetClass: string; Ontology: IXMLDocument) : string;
      function AddDeviceToLocation(LocationNode: IXMLNode; Device: PDevice; Location: PLocation) : boolean;
      function CreateNewLocation(ImplementationLocation: IXMLNode; Location: PLocation) : IXMLNode;
      function IsOurInstance(InstanceNode: IXMLNode) : boolean;
      function ExtractInstanceInformation(InstanceNode: IXMLNode; InstanceInfo: PInstance) : boolean;
    end;

  const

    NameSpaceRDF = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#';
    NameSpaceRDFS = 'http://www.w3.org/2000/01/rdf-schema#';
    NameSpaceOWL = 'xmlns:owl="http://www.w3.org/2002/07/owl#"';
    NameSpaceBASE = 'http://www.owl-ontologies.com/Ontology1241530166.owl#';
    SubscribeText = 'Zarizeni pridano programem AccOntology';

  var
    WholeNodeList: IXMLNodeList;

    MatchingSuspended: boolean;
    LastFoundNode: IXMLNode;    // jez si bude pamatovat posledni nalezeny node (obdoba strtok) - neni nazbyt
                                // musim vytvorit tyhle hnusne globalni promennou :(

implementation

  // interfacove metody


  // GetClassLeafs:
  //    Metoda, ktera poskytne k zadanemu elementu, vsechny jeho listy
  
  function TOntoCore.GetClassLeafs(const Ontology: IXMLDocument; const ClassName: string; Leafs: PStringList) : integer;
  var
    Node: IXMLNode;
    Nodes: IXMLNodeList;
  begin

    Nodes := Ontology.ChildNodes;
    node := nodes.FindNode('RDF', NameSpaceRDF);

    if (node <> nil) then
    begin
      Nodes := Node.ChildNodes;
      WholeNodeList := Nodes; // puvodni seznam vsech uzlu - jeste se bude hodit
      GetClassLeafs := FindClassLeafsRecursively(Nodes, ClassName, Leafs);
    end
    else
      GetClassLeafs := -1; // xml se nezda byt v poradku
  end;


  // GetElementInstances:
  //    Metoda, ktera poskytne vsechny instance zadaneho elementu

  function TOntoCore.GetClassInstances(const Ontology: IXMLDocument; const Element: string; Instances: PInstances) : integer;
  var
    node: IXMLNode;
    nodes: IXMLNodeList;
  begin

    Nodes := Ontology.ChildNodes;
    node := nodes.FindNode('RDF', NameSpaceRDF);

    if (node <> nil) then
    begin
      Nodes := Node.ChildNodes;
      WholeNodeList := Nodes;
      GetClassInstances := FindInstancesOf(Nodes, Element, Instances);
    end
    else
      GetClassInstances := -1; // xml se nezda byt v poradku

  end;

  // privatni metody

  // FindClassLeafsRecursively:
  //    Rekurzivni prohledavaci funkce:
  //      1: Nejprve najde prvni element, ktery je podmnozinou zadaneho elementu
  //      2: Pokud jej najde, overi, zda-li sam tento element neni pouze uzlem
  //        Pokud ano - nic nepridavame - rekurze jiz vhodny list pridala drive
  //        Pokud ne - zjistime jmeno elementu (listu), ktery jsme prave nasli a
  //                   pridame ho do seznamu

  function TOntoCore.FindClassLeafsRecursively(const NodeList: IXMLNodeList; ClassName: string; Leafs: PStringList) : integer;
  var
    node:  IXMLNode;
    size, matches: integer;
    foundLeaf: string;
  begin

    if ((NodeList <> nil) and (NodeList.Count > 0)) then
    begin
      matches := 0;
      node := NodeList.First;
      while (node <> nil) do // pro vsechny uzly v ontologyi
      begin

        // nejprve rekurzivne prohledej deti aktualniho uzlu
        size := FindClassLeafsRecursively(node.ChildNodes, ClassName, Leafs);
        matches := matches + size;

        // potom zkontroluj aktualni uzel
        if (CompareText(node.NodeName, 'rdfs:subClassOf') = 0) then
        begin
          foundLeaf := FindClassInNode(Node, ClassName);
          if (foundLeaf <> '') then
          begin
            // Vyborne! Nasli jsme uzel, ktery je potomkem zadaneho elementu!
            // Nicmene, muze to byt stale jen uzel, nikoliv list! Tudiz, nejprve
            // overme, zda-li se opravdu jedna o list rekurzivnim prohledanim
            // celeho puvodniho seznamu
            size := FindClassLeafsRecursively(WholeNodeList, foundLeaf, Leafs);
            matches := matches + size;

            if (size = 0) then // ok, zda se, ze jsme nalezli list!
            begin
              Leafs^.Add(GetExactName(foundLeaf)); // Pridej ho tedy do seznamu!
              matches := matches + 1;
            end;
          end;
        end;

        node := node.NextSibling;

      end;

      FindClassLeafsRecursively := matches; // navrat, pro jistotu, pocet nalezu

    end // if ((NodeList <> nil) and (NodeList.Count > 0)) then
    else
    begin
      FindClassLeafsRecursively := 0; // zadany seznam uzlu je prazdny - neni co delat
    end;

  end;


  // FindLeafName:
  //    Pokusi se zjistit, zda zadany subClassOf element obsahuje odkaz na jmeno hledane
  //    tridy, tudiz je jejim nasledovnikem.

  function TOntoCore.FindClassInNode(const Node: IXMLNode; const Element: string) : string;
  var
    attributeId, attributeAbout, attributeRes, foundAttribId, foundAttribAbout: OleVariant;
    childNodes: IXMLNodeList;
    classNode: IXMLNode;
  begin
    // nejdriv zkontrolujeme, zda-li neni jmeno ukrito primo v attributu uzlu
    attributeRes := node.GetAttributeNS('resource', NameSpaceRDF);
    if (OleVariantToStr(attributeRes) <> '') and (CompareText(OleVariantToStr(attributeRes), GetDecoratedName(Element)) = 0) then
    begin
      foundAttribAbout := node.ParentNode.GetAttributeNS('about', NameSpaceRDF);
      FindClassInNode := OleVariantToStr(foundAttribAbout); // ok, nasli jsme odkaz na jmeno primo v attr.
    end
    else
    begin
      // nenasli jsme - tudiz je mozne ze uzel ma dalsiho potomka, ve kterem muzeme odkaz take najit
      childNodes := Node.ChildNodes;
      classNode := FindNodeInList(childNodes, 'owl:Class');
      if (classNode <> nil) then
      begin
        attributeAbout := classNode.GetAttributeNS('about', NameSpaceRDF);  // muze to byt odkaz
        attributeId := classNode.GetAttributeNS('ID', NameSpaceRDF);        // nebo rovnou i definice
        if ((OleVariantToStr(attributeAbout) <> '') and (CompareText(OleVariantToStr(attributeAbout), GetDecoratedName(Element)) = 0)) or
           ((OleVariantToStr(attributeId) <> '') and (CompareText(OleVariantToStr(attributeId), GetExactName(Element)) = 0)) then
        begin
          foundAttribAbout := node.ParentNode.GetAttributeNS('about', NameSpaceRDF);
          foundAttribId := node.ParentNode.GetAttributeNS('ID', NameSpaceRDF);
          // je to tak trosku hack, ale usetri to trosku prace
          FindClassInNode := concat(OleVariantToStr(foundAttribAbout), OleVariantToStr(foundAttribId)); // tak a je to, je to potomek
        end
        else
        begin
          FindClassInNode := ''; // nemeli jsme stesti - tento uzel neni potomkem zadane tridy
        end;
      end;
    end;
  end;

  // FindInstancesOf:
  //    Prohleda celou ontologii a pokusi se najit vsechny instance zadane
  //    tridy.

  function TOntoCore.FindInstancesOf(const NodeList: IXmlNodeList; const ClassName: string; Instances: PInstances) : integer;
  var
    node:  IXMLNode;
    size, matches: integer;
    attribute: OleVariant;
    instanceInfo: TInstance;
  begin

    if ((NodeList <> nil) and (NodeList.Count > 0)) then
    begin
      matches := 0;
      node := NodeList.First;
      while (node <> nil) do // iteruje celym seznam nodu
      begin

        // nejprve se zanorime k potomkum
        size := FindInstancesOf(node.ChildNodes, ClassName, Instances);
        matches := matches + size;

        // potom se podivame na samotny uzel, na kterym jsme
        if (CompareText(node.NodeName, ClassName) = 0) then
        begin

            ExtractInstanceInformation(node, @instanceInfo); // ziskejme informace o instanci
            Instances^.Add(instanceInfo); // pridejme ji do seznamu
            matches := matches + 1;

        end;

        node := node.NextSibling;

      end;

      FindInstancesOf := matches; // pro jistotu navratime pocet nalezenych instanci

    end // if ((NodeList <> nil) and (NodeList.Count > 0)) then
    else
    begin
      FindInstancesOf := 0; // seznam uzlu je prazdny, opustme tuto rekurzi
    end;

  end;


  // AddDeviceToGenericOntology:
  //    Metoda pouze vytvori novou kopii genericke ontologie a spusti nad ni pridani
  //    daneho zarizeni

  function TOntoCore.AddDeviceToGenericOntology(GenericOntology: IXMLDocument; Device: PDevice; Location: TLocation; Ontology: IXMLDocument) : string;
  var
    DesiredNode: IXMLNode;
    Success: boolean;
  begin

    Success := GetAdditionalLocationInfo(@Location, Ontology);

    if ((Success) and (FindSpecificNode(GenericOntology.ChildNodes, IsImplementationLocation, Nil, DesiredNode))) then
      Success := VerifyGenericPartOfOntology(GenericOntology, Device, @Location);

    if (Success) and (WriteDeviceImplementationGeneric(DesiredNode, Device, @Location)) then
      AddDeviceToGenericOntology := SaveOntologyElsewhere(GenericOntology)
    else
      AddDeviceToGenericOntology := '';

  end;


  // CreateCopyOfXMLDocument:
  //    Vytvori kopii genericke ontologie a vrati ukazatel na jejiho noveho
  //    drzitele
  //     Vytvaret kopii trosku inteligentneji ...
  //      - Nicmene se zda, ze zatim je tento zpusob postacujici

  function TOntoCore.SaveOntologyElsewhere(Ontology: IXMLDocument) : string;
  begin

    if (not DirectoryExists('./temp')) then
      CreateDir('./temp');
    Ontology.SaveToFile('./temp/generic.owl');

    SaveOntologyElsewhere := './temp/generic.owl';
  end;


  // AddDeviceToOntology:
  //    Metoda napadne podobna metode AddDeviceToGenericOngology - tato nicmene
  //    nevytvari novou kopii souboru owl a ani na ni nevraci cestu

  function TOntoCore.AddDeviceToOntology(Ontology: IXMLDocument; Device: PDevice; Location: TLocation) : boolean;
  var
    DesiredNode: IXMLNode;
    Success: boolean;
  begin

    Success := true;

    if (Location.IsInstance) then
      Success := GetAdditionalLocationInfo(@Location, Ontology);

    if ((Success) and (FindSpecificNode(Ontology.ChildNodes, IsImplementationLocation, Nil, DesiredNode))) then
      Success := VerifyGenericPartOfOntology(Ontology, Device, @Location);

    if (Success) and (WriteDeviceImplementation(DesiredNode, Device, @Location)) then
      AddDeviceToOntology := true
    else
      AddDeviceToOntology := false;

  end;


  // FindSpecificNode:
  //  Zkusme napsat jednu o neco mocnejsi funkci, nez ty ostatni ...
  //  Funkce v zadanem seznamu uzlu zkusi najit rekurzivne uzel, ktery splnuje
  //  Criterium, overene pomoci funkce, kterou dostane jako dalsi parametr
  //  Nalezeny je pak ulozen v parametru FoundNode
  //  Funkce nyni umoznuje i pracovat podobne jako Ceckovsky strtok - umoznuje pokracovat
  //  v hledani od posledniho nalezeneho uzlu.
  //
  //  Je tedy pravdou, ze pomoci teto funkce bychom mohli refaktorovat predchozi
  //  funkce, pro hledani listu, nebo instanci, ale neni pro to cas. A pokud neco funguje ...

  function TOntoCore.FindSpecificNode(ExploredNodes: IXMLNodeList; Criterium: PToFunction; Data: Pointer; var FoundNode: IXMLNode; StartOver: boolean = true) : boolean;
  var
    Node: IXMLNode;
    Found: Boolean;
  begin

    Found := false;
    // pokud explicitne rekneme, ze chceme pokracovat od posledniho nalezu
    //  funkce nastavi globalni promennou MatchingSuspended na false (nahrada staticke promenne)
    //  A od teto chvile funkce nenachazi nody podle kriteria, dokud nenarazi na ten,
    //  ktery je oznacen jako posledni nalezeny
    if (not StartOver) then MatchingSuspended := true;

    if ((ExploredNodes <> nil) and (ExploredNodes.Count > 0)) then
    begin

      Node := ExploredNodes.First;

      while ((Node <> nil) and (not Found)) do
      begin

        // Nejdriv se podivej hloubeji
        Found := FindSpecificNode(Node.ChildNodes, Criterium, Data, FoundNode);

        // Potom se podivej na aktualni uzel
        if (((not Found) and (Criterium(Node, Data))) and (not MatchingSuspended)) then
        begin

          FoundNode := Node;
          LastFoundNode := Node;
          Found := true;

        end;

        if ((MatchingSuspended) and (Node = LastFoundNode)) then
          // Nyni jsme nasli uzel, ktery byl posledni nalezeny - obnovme vyhledavani
          MatchingSuspended := false;

        Node := Node.NextSibling;

      end;

    end;

    FindSpecificNode := Found;

  end;


  // IsImplementationLocation
  //    Overi, zda zadany uzel je nami hledanou lokaci pro zapsani implementace

  function TOntoCore.IsImplementationLocation(CheckedNode: IXMLNode; Data: Pointer) : boolean;
  var
    Success: boolean;
    ChildNode: IXMLNode;
  begin

    Success := false;

    if (CheckedNode <> nil) then // radeji opatrne
    begin

      ChildNode := CheckedNode.ChildNodes.First;

      while ((ChildNode <> nil) and (not Success)) do // zkontrolujme deti nasi nody
      begin

        if ((CompareText(ChildNode.NodeName, 'rdfs:comment') = 0) and
            (CompareText(ChildNode.Text, 'SDF') = 0)) then
        begin

          Success := true; // Ano! Je to nami hledane misto!

        end;

        ChildNode := ChildNode.NextSibling;

      end;

    end;

    IsImplementationLocation := Success;

  end;


  // GetAdditionalLocationInfo:
  //    Pokud neni zadana lokace instanci - vytvori pro novou instanci vhodne jmeno

  function TOntoCore.GetAdditionalLocationInfo(Location: PLocation; Ontology: IXMLDocument) : boolean;
  var
    NewInstanceName: string;
    Success: boolean;
  begin

    Success := true;

    if (not Location^.IsInstance) then
    begin

      NewInstanceName := GetNextInstanceOf(Location^.Levels[Location^.Levels.Count - 1], Ontology);
      Location^.Name := NewInstanceName;
      Success := (NewInstanceName <> '');

    end;

    GetAdditionalLocationInfo := Success;

  end;


  // WriteDeviceImplementationGeneric:
  //    Zapise implementaci zarizeni do genericke ontologie. Tato uloha by mela byt velmi jednoducha
  //    a nemela by zabrat prilis casu straveneho nad vysokou obecnosti.
  //    Tudiz tento kod pisi namiru ...

  function TOntoCore.WriteDeviceImplementationGeneric(ImplementationLocation: IXMLNode; Device: PDevice; Location: PLocation) : boolean;
  var
    Node: IXMLNode;
    Success: boolean;
  begin

    Success := true;

    try

      Node := ImplementationLocation.AddChild('owl:sameAs'); // vytvor pocatecni tag
      Node := Node.AddChild('Level', NameSpaceBASE); // zacni, jako obvykle, s patrem

      Node.Attributes['rdf:ID'] := Location^.Levels[1]; // jmeno instance jsme dostali parametrem
      Node := Node.AddChild('containedIn');
      Node := Node.AddChild('Object');
      Node.Attributes['rdf:ID'] := Location^.Levels[0];
      Node := Node.AddChild('contains');
      Node.Attributes['rdf:resource'] := concat('#',Location^.Levels[1]);

      Node := Node.ParentNode.ParentNode.ParentNode;
      Node := Node.AddChild('contains'); // nastesti mala ontologie obsahuje jen jednu mistnost
      Node := Node.AddChild(Location^.Levels[2]); // Jakeho typu je nase mistnost?
      Node.Attributes['rdf:ID'] := Location^.Name;
      Node := Node.AddChild('containedIn');
      Node.Attributes['rdf:resource'] := concat('#',Location^.Levels[1]);

      AddDeviceToLocation(Node.ParentNode, Device, Location);

    except

      Success := false; // doslo k zavazne chybe, pridani nebylo uspesne :(

    end;

    WriteDeviceImplementationGeneric := Success;

  end;


  // AddFunctionsToDeviceGeneric:
  //    Metoda prida jednotlive funkce k zadane implementaci zarizeni

  function TOntoCore.AddFunctionsToDeviceGeneric(TargetNode: IXMLNode; Device: PDevice) : boolean;
  var
    Success: boolean;
    I: integer;
    Node: IXMLNode;
    SingleFunc: PFunction;
  begin

    Success := true;

    try

      I := 1;
      SingleFunc := @Device^.Functions[I];
      while ((SingleFunc^.Name <> '') and (I <= 30)) do
      begin

        Node := TargetNode.AddChild('disposeOf');
        Node := Node.AddChild(SingleFunc^.Name);
        Node.Attributes['rdf:ID'] := GetNextInstanceOf(SingleFunc^.Name, TargetNode.OwnerDocument);
        Node := Node.AddChild('usedBy');
        Node.Attributes['rdf:resource'] := GetDecoratedName(TargetNode.Attributes['rdf:ID']);

        inc(I);
        SingleFunc := @Device^.Functions[I];
      end;

    except

      Success := false;

    end;

    AddFunctionsToDeviceGeneric := Success;

  end;


  // AddPropertiesToDeviceGeneric:
  //    Funkce prida jednotlive property k zadane implementaci zarizeni
 
  function TOntoCore.AddPropertiesToDeviceGeneric(TargetNode: IXMLNode; Device: PDevice) : boolean;
  var
    Success: boolean;
    I: integer;
    Node: IXMLNode;
    SingleProp: PProperty;
  begin

    Success := true;

    try

      I := 1;
      SingleProp := @Device^.Properties[I];
      while ((SingleProp^.Name <> '') and (I <= 3)) do
      begin

        if (SingleProp^.Allowed) then
        begin
          Node := TargetNode.AddChild('hasProperty');
          Node := Node.AddChild(SingleProp^.Name);
          Node.Attributes['rdf:ID'] := GetNextInstanceOf(SingleProp^.Name, TargetNode.OwnerDocument);
          Node := Node.AddChild('belongsTo');
          Node.Attributes['rdf:resource'] := GetDecoratedName(TargetNode.Attributes['rdf:ID']);
        end;

        inc(I);
        SingleProp := @Device^.Properties[I];
      end;

    except

      Success := false;

    end;

    AddPropertiesToDeviceGeneric := Success;

  end;


  // VerifyGenericPartOfOntology:
  //    Slozitejsi funkce, ktera se v zasade pokusi o vyhledani obecny definic vsech pouzitych trid v ontologii
  //    pro dane zarizeni a pripadne chybejici do ontologie doplni

  function TOntoCore.VerifyGenericPartOfOntology(Ontology: IXMLDocument; Device: PDevice; Location: PLocation) : boolean;
  var
    Success: boolean;
    FoundNode: IXMLNode;
    SubClassOf: TStringList;
  begin

    Success := true;

    try

      SubClassOf := TStringList.Create();

      // kontrola existence typu zarizeni:
      if (not FindSpecificNode(Ontology.ChildNodes, IsClassDefinitionOrReference, @Device^.DeviceType, FoundNode)) then
      begin
        SubClassOf.Clear();
        SubClassOf.Add(Device^.Category);
        SubClassOf.Add('Device');
        AddNewClass(Ontology, Device^.DeviceType, @SubClassOf);
      end;

      // kontrola existence jednotlivych funkci
      VerifyGenericDefinitionOfFunctions(Ontology, Device);

      // kontrola existence jednotlivych property
      VerifyGenericDefinitionOfProperties(Ontology, Device);

      // Zde budu proste predpokladat, ze AccurateLocation definovana je, protoze
      //  jeji definici nerozumim :(

      // Pokud jsme se dostali az sem, muzeme predpokladat, ze veryfikace a
      // fixovani probehlo uspesne

    except

      Success := false; // pokud nam vylitla vyjimka, neco je spatne a verifikace
                        // se tudiz nezdarila ...

    end;

    VerifyGenericPartOfOntology := Success;

  end;


  // IsObjectDefinitionOrReference:
  //    Funkce overi, zda je zadany uzel definici instance tridy nebo referenci na ni

  function TOntoCore.IsObjectDefinitionOrReference(CheckedNode: IXMLNode; Data: Pointer) : boolean;
  var
    ObjectName: PString;
    Found: boolean;
  begin

    Found := false;

    try

      ObjectName := Data;

      if (CheckedNode.NodeType = ntElement) then
      begin

        if ((CompareText(OleVariantToStr(CheckedNode.GetAttributeNS('ID', NameSpaceRDF)), ObjectName^) = 0) or
           (CompareText(OleVariantToStr(CheckedNode.GetAttributeNS('about', NameSpaceRDF)), GetDecoratedName(ObjectName^)) = 0)) then
        begin

          Found := true;

        end;

      end;

    except
       ;
    end;

    IsObjectDefinitionOrReference := Found;

  end;


  // IsObjectDefinition:
  //    Funkce overi, zda je zadany uzel definici instance tridy

  function TOntoCore.IsObjectDefinition(CheckedNode: IXMLNode; Data: Pointer) : boolean;
  var
    ObjectName: PString;
    Found: boolean;
  begin

    Found := false;

    try

      ObjectName := Data;

      if (CheckedNode.NodeType = ntElement) then
      begin

        if (CompareText(OleVariantToStr(CheckedNode.GetAttributeNS('ID', NameSpaceRDF)), ObjectName^) = 0) then
        begin

          Found := true;

        end;

      end;

    except
       ;
    end;

    IsObjectDefinition := Found;
  end;


  // IsClassDefinitionOrReference:
  //    Funkce overi, zda je zadany uzel definici tridy nebo odkazem na ni (nikoliv vsak jeji instance)

  function TOntoCore.IsClassDefinitionOrReference(CheckedNode: IXMLNode; Data: Pointer) : boolean;
  var
    ClassName: PString;
    Found: boolean;
  begin

    Found := false;

    try

      if (CompareText(CheckedNode.NodeName, 'owl:Class') = 0) then // zde nas zajimaji jen tridy!
      begin

        ClassName := Data;

          if ((CompareText(OleVariantToStr(CheckedNode.GetAttributeNS('ID', NameSpaceRDF)), ClassName^) = 0) or
             (CompareText(OleVariantToStr(CheckedNode.GetAttributeNS('about', NameSpaceRDF)), GetDecoratedName(ClassName^)) = 0)) then
          begin

            Found := true;

          end;

      end;
      
    except
       ;
    end;

    IsClassDefinitionOrReference := Found;
  end;


  // VerifyGenericDefinitionOfProperties:
  //    Funkce overi, zda jsou vsechny pouzte property obecne definovany v ontologii
  //    Pokud ne, vytvori je na zaklade ziskanych informaci o property z configu

  function TOntoCore.VerifyGenericDefinitionOfProperties(Ontology: IXMLDocument; Device: PDevice) : boolean;
  var
    Success: boolean;
    I: integer;
    Node: IXMLNode;
    SingleProp: PProperty;
    Levels: TStringList;
  begin

    Success := true;

    try

      Levels := TStringList.Create();
      I := 1;
      SingleProp := @Device^.Properties[I];
      while ((SingleProp^.Name <> '') and (I <= 3)) do
      begin

        if (not FindSpecificNode(Ontology.ChildNodes, IsClassDefinitionOrReference, @SingleProp^.Name, Node)) then
        begin
          Levels.Add('Property');
          AddNewClass(Ontology, SingleProp^.Name, @Levels);
        end;

        inc(I);
        SingleProp := @Device^.Properties[I];
      end;

    except

      Success := false;

    end;

    VerifyGenericDefinitionOfProperties := Success;
  end;


  // VerifyGenericDefinitionOfFunctions:
  //    Funkce overi, zda jsou vsechny pouzte funkce obecne definovany v ontologii
  //    Pokud ne, vytvori je na zaklade ziskanych informaci o funkci z configu

  function TOntoCore.VerifyGenericDefinitionOfFunctions(Ontology: IXMLDocument; Device: PDevice) : boolean;
  var
    Success: boolean;
    I: integer;
    Node: IXMLNode;
    SingleFunc: PFunction;
  begin

    Success := true;

    try

      I := 1;
      SingleFunc := @Device^.Functions[I];
      while ((SingleFunc^.Name <> '') and (I <= 30)) do
      begin

        if (not FindSpecificNode(Ontology.ChildNodes, IsClassDefinitionOrReference, @SingleFunc^.Name, Node)) then
          AddNewClass(Ontology, SingleFunc^.Name, @SingleFunc^.Levels);

        inc(I);
        SingleFunc := @Device^.Functions[I];
      end;

    except

      Success := false;

    end;

    VerifyGenericDefinitionOfFunctions := Success;
  end;


  // AddNewClass:
  //    Funkce vytvari novou tridu do ontologie. Za timto ucelem je treba nejprve vytvorit
  //    Nove dite v ontologii, ktere bude tuto tridu definovat (pripade vsechny jeji predky)
  //    a nasledne vsem nove vytvorenym tridam vytvorit "disjointy", aby byla definice uplna
  //    a sourozenci navzajem o sobe vedeli
  
  function TOntoCore.AddNewClass(Ontology: IXMLDocument; ClassName: string; SubClassOf: PStringList) : boolean;
  var
    Node, FoundNode, NewClassNode: IXMLNode;
    SubClass, SubSubClass: string;
    i: Integer;
    Success: boolean;
  begin

    Success := false;

    // predpokladame, ze nejnizsi level definovany je (funkce, zarizeni, etc...)
    // pokud ne, nema cenu dal pokracovat
    SubClass := SubClassOf^.Strings[0];
    if (SubClassOf <> nil) and (FindSpecificNode(Ontology.DocumentElement.ChildNodes, IsClassDefinitionOrReference, @SubClass, FoundNode)) then
    begin

      for i := 1 to (SubClassOf^.Count - 1) do // vsimnete si pocatecniho indxu 1 (nikoliv 0)
      begin
        SubSubClass := SubClassOf^.Strings[i-1];
        SubClass := SubClassOf^.Strings[i];

        // nejprve se pokusime doplnit vsechny rodice - pokud nejsou
        if (not FindSpecificNode(Ontology.DocumentElement.ChildNodes, IsClassDefinitionOrReference, @SubClass, FoundNode)) then
        begin
          NewClassNode := Ontology.DocumentElement.AddChild('owl:Class');
          NewClassNode.Attributes['rdf:ID'] := SubClass;
          Node := NewClassNode.AddChild('rdfs:subClassOf');
          Node := Node.AddChild('owl:Class');
          Node.Attributes['rdf:about'] := concat('#', SubSubClass);
          AddDisjoints(Ontology, SubClass, SubSubClass, NewClassNode);
        end;
      end;

      // nasledne udelame noveho syna :)
      NewClassNode := Ontology.DocumentElement.AddChild('owl:Class');
      NewClassNode.Attributes['rdf:ID'] := ClassName;
      Node := NewClassNode.AddChild('rdfs:subClassOf');
      Node := Node.AddChild('owl:Class');
      Node.Attributes['rdf:about'] := concat('#', SubClass); // trosku naivne predpokladam ze v SubClass zustalo to spravne
      AddDisjoints(Ontology, ClassName, SubClass, NewClassNode);

      Success := true;

    end;

    AddNewClass := Success;

  end;


  //  AddDisjoints:
  //    Metoda prida zadane tride infomace o jeijch sourozencich, tak jako prida
  //    vsen sourozencum infomaci o zadane tride

  function TOntoCore.AddDisjoints(Ontology: IXMLDocument; OfClass: string; OnSubClassOf: string; NewClassNode: IXMLNode) : boolean;
  var
    Node, DisjointWith: IXMLNode;
    Found, Success: Boolean;
    Disjoints: TStringList;
    i: Integer;
    ClassName: string;
  begin

    Success := true;

    try

      Disjoints := TStringList.Create(); // zde si uchovame vsechny nalezene tridy, na stejne urovni
                                         // ty pozdeji pridame jako disjointy k nove pridane tridy

      Found := FindSpecificNode(Ontology.DocumentElement.ChildNodes, IsChildOf, @OnSubClassOf, Node, true);

      // nejprve pridej jeden disjoint do kazde dalsi classy odvozene od nasi nove
      // a vsechny si je zapamatuj
      while (Found) do
      begin

        if (CompareText(OleVariantToStr(Node.GetAttributeNS('ID', NameSpaceRDF)), OfClass) <> 0) then
        begin
          DisjointWith := Node.AddChild('owl:disjointWith');
          DisjointWith.Attributes['rdf:resource'] := concat('#', OfClass);
          ClassName := concat(OleVariantToStr(Node.GetAttributeNS('ID', NameSpaceRDF)), OleVariantToStr(Node.GetAttributeNS('about', NameSpaceRDF))); // fuj, ja vim, no ...
          Disjoints.Add(GetDecoratedName(ClassName));
        end;

        Found := FindSpecificNode(Ontology.DocumentElement.ChildNodes, IsChildOf, @OnSubClassOf, Node, false);
      end;

      // nyni pridej do nasi nove classy odkazy na vsechny ostatni
      for i := 0 to (Disjoints.Count - 1) do
      begin
        DisjointWith := NewClassNode.AddChild('owl:disjointWith');
        DisjointWith.Attributes['rdf:resource'] := Disjoints[i];
      end;

    except

      Success := true;

    end;

    AddDisjoints := Success;

  end;

  // IsChildOf:
  //    Zjisti, zda je dany uzel ditetem uzlu, predaneho parametrem Data

  function TOntoCore.IsChildOf(CheckedNode: IXMLNode; Data: Pointer) : boolean;
  var
    Parent: PString;
    Success: boolean;
    Node, ChildNode: IXMLNode;
  begin

    Success := false;

    try

      Parent := PString(Data);

      if (CompareText(CheckedNode.NodeName, 'owl:Class') = 0) then
      begin

        Node := FindNodeInList(CheckedNode.ChildNodes, 'rdfs:subClassOf');

        if (Node <> nil) then
        begin

          ChildNode := Node.ChildNodes.First;

          if ((ChildNode <> nil) and (CompareText(ChildNode.NodeName, 'owl:Class') = 0)) then
          begin
            if (CompareText(OleVariantToStr(ChildNode.GetAttributeNS('ID', NameSpaceRDF)), Parent^) = 0) then
              Success := true;
            if (CompareText(OleVariantToStr(ChildNode.GetAttributeNS('about', NameSpaceRDF)), concat('#', Parent^)) = 0) then
              Success := true;
            if (CompareText(OleVariantToStr(ChildNode.GetAttributeNS('resource', NameSpaceRDF)), concat('#', Parent^)) = 0) then
              Success := true;
          end
          else
          begin

            if (CompareText(OleVariantToStr(Node.GetAttributeNS('resource', NameSpaceRDF)), concat('#', Parent^)) = 0) then
              Success := true;
            if (CompareText(OleVariantToStr(Node.GetAttributeNS('about', NameSpaceRDF)), concat('#', Parent^)) = 0) then
              Success := true;
            if (CompareText(OleVariantToStr(Node.GetAttributeNS('ID', NameSpaceRDF)), Parent^) = 0) then
              Success := true;

          end;

        end;

      end;

    except

      ; // byli jsme opatrni a vyplatilo se ...

    end;

    IsChildOf := Success;

  end;


  // WriteDeviceImplementation:
  //    Na zadane misto se pokusi zapsat implementaci pozadovaneho zarizeni

  function TOntoCore.WriteDeviceImplementation(ImplementationLocation: IXMLNode; Device: PDevice; Location: PLocation) : boolean;
  var
    Success: boolean;
    Node, FoundLocation: IXMLNode;
  begin

    Success := true;

    try

      if (Location^.IsInstance) then
      begin

        if (FindSpecificNode(ImplementationLocation.ChildNodes, IsObjectDefinitionOrReference, @Location^.Name, FoundLocation)) then
        begin

          // Zadana lokace je jiz existujici instanci - tu jsme nasli a pridame tam nase zarizeni
          AddDeviceToLocation(FoundLocation, Device, Location);

        end;

      end
      else
      begin

        //  Zadana lokace jeste neexistuje - vytvorme tedy novou lokaci a do ni pak teprve zarizeni pridejme
        AddDeviceToLocation(CreateNewLocation(ImplementationLocation, Location), Device, Location);

      end;

    except

      Success := false;

    end;

    WriteDeviceImplementation := Success;
  end;
  

  // GetNextInstanceOf:
  //    Funkce najivne zjisti jmeno dalsi instance zadane tridy:
  //    Funguje tak, ze nejprve najde vsechny jeji instance (reusneme jiz existujici fci)
  //    A s tim predpokladem, ze instance se pojmenovavaji: "Trida_X" kde x je poradove cislo
  //    instance, pricteme k celkovemu poctu instanci 1ku a toto cislo pak pripojime k
  //    nazvu tridy
  //
  //    Metoda tedy predpoklada neprerusenou ciselnou radu pojmenovavani instanci
  //    Neni to tedy neprustrelne, ale je to prozatim postacujici
  //
  //    Vsimnete si take prosim, ze komentar k metode je delsi nez metoda sama :D

  function TOntoCore.GetNextInstanceOf(TargetClass: string; Ontology: IXMLDocument) : string;
  var
    Instances: TInstances;
  begin

    // nejprve ziskame seznam vsechn instanci
    GetClassInstances(Ontology, TargetClass, @Instances);

    // vysledne cislo zjistime velice naivne!
    GetNextInstanceOf := concat(TargetClass, '_', IntToStr(Instances.GetSize() + 1));

  end;


  // AddDeviceToLocation:
  //    Funkce zapise implementaci zarizeni na zadanou lokaci do "velke" ontologie

  function TOntoCore.AddDeviceToLocation(LocationNode: IXMLNode; Device: PDevice; Location: PLocation) : boolean;
  var
    Success: boolean;
    Node, DeviceNode: IXMLNode;
    DeviceInstanceName: string;
  begin

    Success := true;

    try

      Node := LocationNode.AddChild('has');
      DeviceNode := Node.AddChild(Device^.DeviceType);
      DeviceInstanceName := GetNextInstanceOf(Device^.DeviceType, LocationNode.OwnerDocument);
      DeviceNode.Attributes['rdf:ID'] := DeviceInstanceName;
      Node := DeviceNode.AddChild('isIn');
      Node.Attributes['rdf:resource'] := GetDecoratedName(Location^.Name);

      Success := false;
      if (AddFunctionsToDeviceGeneric(DeviceNode, Device)) then // Pridejme funkce
        if (AddPropertiesToDeviceGeneric(DeviceNode, Device)) then // Pridejme parametry
          Success := true;

      if ((Success) and (Device^.HasAccurateLocality)) then
      begin
        Node := DeviceNode.AddChild('isExactlyIn');
        Node := Node.AddChild('AccurateLocation');
        Node.Attributes['rdf:ID'] := GetNextInstanceOf('AccurateLocation', LocationNode.OwnerDocument);
        Node := Node.AddChild('determinates');
        Node.Attributes['rdf:resource'] := GetDecoratedName(DeviceInstanceName);

      end;

      // TODO<MERXBJ> Temporarily disabled!!!!!!!!!!
      //  Prozatim zustava disabled, protoze porad presne nevime, jak vocabulary funguji ...

      if ((Success) and (Device^.Vocabulary.Path <> '')) and false then
      begin
        Node := DeviceNode.AddChild('hasAssociatedVocabulary');
        Node := Node.AddChild('');
      end;

      // Jeste navic si zarizeni oznacime tak, abychom ho poznali
      Node := DeviceNode.AddChild('rdfs:comment');
      Node.Attributes['rdf:datatype'] := 'http://www.w3.org/2001/XMLSchema#string';
      Node.Text := SubscribeText;

    except

      Success := false;

    end;

    AddDeviceToLocation := Success;

  end;


  // CreateNewLocation:
  //    Funkce vytvori novou instanci lokace, na zaklade zadanych parametru
  //    Funkce take predpoklada, ze v ontologii existuje patro, object, etc...

  function TOntoCore.CreateNewLocation(ImplementationLocation: IXMLNode; Location: PLocation) : IXMLNode;
  var
    Level, Space, Contains, ContainedIn: IXMLNode;
    LevelName: string;
  begin

    Space := nil;
    LevelName := Location^.Levels[1];
    // Ve jmene lokace se nyni ukryva "Nova Mistnost" - zjistime tedy jmeno nove instance
    // A to pak ulozime do configurace lokace namisto "Nova Mistnost"
    // TODO: Je ale zvlastni, ze uz to nenapravila metoda GetAdditionalLocationInfo
    Location^.Name := GetNextInstanceOf(Location^.Levels[Location^.Levels.Count - 1], ImplementationLocation.OwnerDocument);

    if (FindSpecificNode(ImplementationLocation.ChildNodes, IsObjectDefinition, @LevelName, Level)) then
    begin

      // jsem na tagu, ktery definuje level - pridejme tedy teto urovni novou mistnost
      Contains := Level.AddChild('contains');
      Space := Contains.AddChild(Location^.Levels[Location^.Levels.Count - 1]);
      Space.Attributes['rdf:ID'] := Location^.Name;
      ContainedIn := Space.AddChild('containedIn');
      ContainedIn.Attributes['rdf:resource'] := GetDecoratedName(Location^.Levels[1]);

    end;

    CreateNewLocation := Space;

  end;


  // RemoveDeviceFromOntology:
  //    Tato funkce proste a jen smaze cely element definujici zarizeni ... vse ostatni zustane -
  //    Tezko zjistovat, co vsechno prislo s pridanim noveho zarizeni (ano slo by to, ale to uz je jiny pribeh)
  //
  //    Funkce tedy umi spravne mazat jen nami vytvorena zarizeni

  function TOntoCore.RemoveDeviceFromOntology(Ontology: IXMLDocument; DeviceInstance: PInstance) : boolean;
  var
    DeviceImplementation: IXMLNode;
  begin

    // nejprve zarizeni v ontologii najdeme
    if (FindSpecificNode(Ontology.ChildNodes, IsObjectDefinition, @(DeviceInstance^.Name), DeviceImplementation)) then
    begin

      // pro nase potreby je to zatim snad postacujici volani
      DeviceImplementation.ParentNode.ParentNode.ChildNodes.Remove(DeviceImplementation.ParentNode);

    end;

  end;


  // IsOurInstance:
  //    Funkce zjisti, zda-li je zadana instance oznacena, jako nami vytvorena
  
  function TOntoCore.IsOurInstance(InstanceNode: IXMLNode) : boolean;
  var
    RetVal: boolean;
    Node: IXMLNode;
  begin

    RetVal := false;

    try

      Node := FindNodeInList(InstanceNode.ChildNodes, 'rdfs:comment');

      if (Node <> nil) then
        if (CompareText(Node.Text, SubscribeText) = 0) then
          RetVal := true;

    except

      ;

    end;

    IsOurInstance := RetVal;

  end;


  // ExtractInstanceInformation
  //    Funkce ziska presne informace o zadane instanci a ulozi je do tridy TInstance

  function TOntoCore.ExtractInstanceInformation(InstanceNode: IXMLNode; InstanceInfo: PInstance) : boolean;
  var
    attribute: String;
  begin

    if (InstanceNode <> nil) then
    begin

      attribute := InstanceNode.GetAttributeNS('ID', NameSpaceRDF);

      instanceInfo^.name := GetExactName(OleVariantToStr(attribute));
      instanceInfo^.CreatedByThisApp := IsOurInstance(InstanceNode);

    end;

  end;

end.
