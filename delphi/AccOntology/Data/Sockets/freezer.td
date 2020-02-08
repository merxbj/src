<?xml version="1.0" encoding="UTF-8"?>
<target 
  about="http://res.i2home.org/chain/freezer" 
  id="target" 
  hidden="false" 
  xmlns="http://myurc.org/ns/targetdesc" 
  xmlns:ext="http://www.i2home.org/ext" 
  xmlns:dc="http://purl.org/dc/elements/1.1/" 
  xmlns:dcterms="http://purl.org/dc/terms/" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xsi:schemaLocation="http://myurc.org/ns/targetdesc http://myurc.org/ns/targetdesc http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd">

  <dc:creator>Philipp Titze, Siemens AG</dc:creator>
  <dc:contributor>Juergen Goerlich, Siemens AG</dc:contributor>
  <dc:publisher>Siemens AG, Germany</dc:publisher>
  <dc:rights>Copyright 2008, Siemens AG</dc:rights>
  <dc:title xml:lang="en">Target Description for server@home freezer.</dc:title>
  <dcterms:conformsTo>http://myurc.org/iso24752-4/2007</dcterms:conformsTo>
  <dcterms:modified>2008-07-17</dcterms:modified>

  <socket id="socket" name="http://res.i2home.org/chain/freezer/socket">
    <socketDescriptionLocalAt>./../targets/saH/freezer.uis</socketDescriptionLocalAt>
  </socket>
  
  <rdf:RDF 
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dcterms="http://purl.org/dc/terms/" 
    xmlns="http://myurc.org/ns/res#">

    <!-- Description of the root Resource Directory -->
    <ResDir>
      <dirItems rdf:parseType="Collection">
        <ResSheet rdf:about="http://res.i2home.org/chain/freezer/freezer.rsheet">
          <forLang>en</forLang>
          <dcterms:conformsTo rdf:resource="http://myurc.org/iso24752-5/2007"/>
          <localAt>./../targets/saH/freezer.rsheet</localAt>
          <dc:type>Text</dc:type>
          <aResDescForDomain rdf:resource="http://res.i2home.org/chain/freezer/socket"/>
        </ResSheet>
      </dirItems>
    </ResDir>
  </rdf:RDF>
</target>
