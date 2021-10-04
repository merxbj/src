using System;
using System.Xml;

namespace ElectionVisualiser
{
    public interface IPartyDataProvider
    {
        public int GetPartyComposition(int partyId);
        public int GetQuorum(int partyComposition);
        public int GetNumCandidatesInRegion(int regionNumber, int partyId);
        public int GetNumCandidates(int partyId);
    }

    public class PartyDataProvider : IPartyDataProvider
    {

        public PartyDataProvider(XmlDocument partyData, XmlDocument candidateData)
        {
            this.partyData = partyData;
            this.candidateData = candidateData;
        }
        public int GetPartyComposition(int partyId)
        {
            var partyCompositionXml = partyData.SelectSingleNode($"PS_RKL/PS_RKL_ROW[KSTRANA = {partyId}]/POCSTRVKO");
            if (partyCompositionXml == null)
            {
                throw new Exception($"Party {partyId} not found in the definition!");
            }

            int partyComposition = 0;
            int.TryParse(partyCompositionXml.InnerText, out partyComposition);
            if (partyComposition <= 0)
            {
                throw new Exception($"Party {partyId} has invalid party composition: {partyComposition}");
            }

            return partyComposition;
        }

        public int GetQuorum(int partyComposition)
        {
            // We will be calculating 2017 results towards 2021 rules anyway
            if (partyComposition == 1) return 5;
            else if (partyComposition == 2) return 8;
            else if (partyComposition >= 3) return 11;
            else throw new Exception($"Invalid party composition {partyComposition}!");
        }

        public int GetNumCandidatesInRegion(int regionNumber, int partyId)
        {
            var candidatesForPartyAndRegionXml = candidateData.SelectNodes($"PS_REGKAND/PS_REGKAND_ROW[VOLKRAJ = {regionNumber} and KSTRANA = {partyId}]");
            if (candidatesForPartyAndRegionXml == null)
            {
                throw new Exception($"Candidates for {partyId} in {regionNumber} not found in the definition!");
            }

            return candidatesForPartyAndRegionXml.Count;
        }

        public int GetNumCandidates(int partyId)
        {
            var candidatesForPartyXml = candidateData.SelectNodes($"PS_REGKAND/PS_REGKAND_ROW[KSTRANA = {partyId}]");
            if (candidatesForPartyXml == null)
            {
                throw new Exception($"Candidates for {partyId} not found in the definition!");
            }

            return candidatesForPartyXml.Count;
        }

        private XmlDocument partyData;
        private XmlDocument candidateData;
    }
}