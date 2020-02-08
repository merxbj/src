namespace Integri.Indexer.PublicNameIndexing.Locating
{
    class ReferencedPublicObject : PublicObject
    {
        public ReferencedPublicObject(PublicObject po, int componentId, int objectIsn)
            : base(po.Name, po.Type, po.MciFile, po.LocalId)
        {
            ComponentId = componentId;
            ObjectIsn = objectIsn;
        }

        public int ComponentId { get; set; }
        public int ObjectIsn { get; set; }
    }
}
