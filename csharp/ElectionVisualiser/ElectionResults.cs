using System;
using System.Collections.Generic;

namespace ElectionVisualiser
{
    class PartyResults
    {
        public PartyResults(Party party, int votes, decimal percentage)
        {
            Party = party;
            Votes = votes;
            Percentage = percentage;
            Mandates = 0;
            RemainingVotes = 0;
        }
        public Party Party { private set; get; }
        public int Votes { private set; get; }
        public decimal Percentage { private set; get; }
        public int Mandates { get; set; }
        public int RemainingVotes { get; set; }
    }

    class RegionResults
    {
        public RegionResults(Region region, int votes)
        {
            Region = region;
            Votes = votes;
            PartyResults = new Dictionary<Party, PartyResults>();
            Mandates = 0;
        }

        public Region Region { private set; get; }
        public int Votes { private set; get; }
        public int VoteNumber { set; get; }
        public int Mandates { get; set; }
        public IDictionary<Party, PartyResults> PartyResults {private set; get; }

        public void AddPartyResults(PartyResults results)
        {
            PartyResults[results.Party] = results;
        }
    }

    class TotalResults
    {
        public TotalResults(int votes)
        {
            Votes = votes;
            RegionResults = new Dictionary<Region, RegionResults>();
            PartyResults = new Dictionary<Party, PartyResults>();
            MandateNumber = 0;
            VoteNumber = 0;
        }

        public IDictionary<Region, RegionResults> RegionResults {private set; get;}
        public IDictionary<Party, PartyResults> PartyResults { private set; get; }
        public int Votes { private set; get; }
        public int MandateNumber { get; set; }
        public int VoteNumber { set; get; }

        public void AddRegionResults(RegionResults results)
        {
            RegionResults[results.Region] = results;
        }

        public void AddPartyResults(PartyResults results)
        {
            PartyResults[results.Party] = results;
        }
    }

    class Party
    {
        public Party(int id) : this(id, String.Empty)
        {
        }

        public Party(int id, string name)
        {
            Id = id;
            Name = name;
        }

        public string  Name {private set; get;}
        public int Id {private set; get;}

        public override bool Equals(object obj)
        {
            return obj is Party party &&
                   Id == party.Id;
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(Id);
        }
    }

    class Region
    {
        public Region(int id) : this(id, String.Empty)
        {
        }

        public Region(int id, string name)
        {
            Id = id;
            Name = name;
        }

        public string Name {private set; get;}
        public int Id {private set; get;}

        public override bool Equals(object obj)
        {
            return obj is Region party &&
                   Id == party.Id;
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(Id);
        }
    }
}