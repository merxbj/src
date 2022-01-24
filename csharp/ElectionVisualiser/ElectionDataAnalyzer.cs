using System;
using System.Xml;
using System.Collections.Generic;

namespace ElectionVisualiser
{
    class ElectionResultsAnalyzer
    {
        public ElectionResultsAnalyzer(IPartyDataProvider partyData)
        {
            this.partyData = partyData;
        }

        public TotalResults CalculateResults(XmlDocument resultsXml)
        {
            TotalResults results = LoadResults(resultsXml);

            ValidateResultsData(results);

            // don't calculate anything while under 2% - results might be ... crazy
            if (results.AreasCompleted >= 2m)
            {
                UpdateRepublicMandateNumber(results);
                UpdateRegionMandates(results);
                ApplyQuorum(results);
                UpdateRegionVoteNumbers(results);
                CalculateFirstScrutiny(results);
                UpdateRepublicResults(results);
                CalculateSecondScrutiny(results);
                UpdateRegionResults(results);
            }

            return results;
        }

        private void UpdateRegionResults(TotalResults results)
        {
            var mandatesToDistribute = new Dictionary<Party, int>();

            foreach (var partyResults in results.PartyResults.Values)
            {
                if (partyResults.PassedQuorum)
                {
                    mandatesToDistribute[partyResults.Party] = partyResults.Mandates;
                }
            }

            foreach (var regionResults in results.RegionResults.Values)
            {
                foreach (var partyResults in regionResults.PartyResults.Values)
                {
                    if (partyResults.PassedQuorum)
                    {
                        mandatesToDistribute[partyResults.Party] -= partyResults.Mandates;
                    }
                }
            }

            foreach (var distribution in mandatesToDistribute)
            {
                Party party = distribution.Key;
                int mandates = distribution.Value;
                if (mandates > 0)
                {
                    DistributeMandatesToRegions(results, party, mandates);
                }
            }
        }

        private void DistributeMandatesToRegions(TotalResults results, Party party, int mandatesToDistribute)
        {
            Random rnd = new Random();
            List<PartyResults> gainingMandate = new List<PartyResults>();
            
            foreach (var regionResults in results.RegionResults.Values)
            {
                gainingMandate.Add(regionResults.PartyResults[party]);
            }

            gainingMandate.Sort((left, right) => {
                int result = right.RemainingVotes.CompareTo(left.RemainingVotes);
                if (result == 0)
                {
                    result = rnd.Next(1) == 1 ? 1 : -1;
                }
                return result;
            });

            for (int i = 0; i < mandatesToDistribute; i++)
            {
                gainingMandate[i % gainingMandate.Count].Mandates += 1;
            }
        }

        private void CalculateSecondScrutiny(TotalResults results)
        {
            UpdateRepublicVoteNumber(results);

            int remainingMandates = 200;

            // note that this is on the central level!
            var partyModulos = new Dictionary<Party, int>();
            var partiesWithEnoughCandidates = new HashSet<Party>();

            foreach (var partyResults in results.PartyResults.Values)
            {
                if (partyResults.PassedQuorum)
                {
                    // for the first step let's assume that all parties have enough candidates
                    partiesWithEnoughCandidates.Add(partyResults.Party);

                    // assign the mandates and update modules for further redistribution
                    partyResults.Mandates += partyResults.RemainingVotes / results.VoteNumber;
                    partyModulos[partyResults.Party] = partyResults.RemainingVotes % results.VoteNumber;

                    remainingMandates -= partyResults.Mandates;
                }
            }

            if (remainingMandates < 0)
            {
                // take away mandates from appropriate parties if we have given too many
                UnassignExcessiveMandates(results, -remainingMandates, partyModulos);
                remainingMandates = 0;
            }
            else if (remainingMandates > 0)
            {
                // if we did not distribute all remaining mandates - apply the "distribution rule"
                AssignRemainingMandates(results, remainingMandates, partyModulos, partiesWithEnoughCandidates);
                remainingMandates = 0;
            }

            // redistributes those that parties don't have enough candidates for
            int mandatesToTakeAway = 0;
            do
            {
                // take mandates away from parties with insufficient candidates and try distribution again
                foreach (var partyResults in results.PartyResults.Values)
                {
                    if (partyResults.PassedQuorum)
                    {
                        int totalCandidates = partyData.GetNumCandidates(partyResults.Party.Id);

                        mandatesToTakeAway += partyResults.Mandates - totalCandidates;
                        if (mandatesToTakeAway > 0)
                        {
                            partyResults.Mandates -= mandatesToTakeAway;
                            partiesWithEnoughCandidates.Remove(partyResults.Party);
                        }
                    }
                }

                if (mandatesToTakeAway > 0 && partiesWithEnoughCandidates.Count > 0)
                {
                    AssignRemainingMandates(results, mandatesToTakeAway, partyModulos, partiesWithEnoughCandidates);
                }
                else if (mandatesToTakeAway > 0)
                {
                    // lol - who knows what happens in the real world (might never happen anyway)
                    // this basically means that there are not enough candidates to fill up all
                    // 200 mandates by all the parties that made it through the quorum
                    // this might happen in the early stages of the vote collection or
                    // ultimately a new elections might be called ... who knows
                    break;
                }

            } while (mandatesToTakeAway > 0);
        }

        private void UnassignExcessiveMandates(TotalResults results, int mandatesToTakeAway, IDictionary<Party, int> partyModulos)
        {
            Random rnd = new Random();
            List<PartyResults> losingMandate = new List<PartyResults>();

            foreach (var losing in results.PartyResults.Values)
            {
                if (losing.PassedQuorum)
                {
                    losingMandate.Add(losing);
                }
            }
    
            losingMandate.Sort((left, right) => {
                int result = partyModulos[left.Party].CompareTo(partyModulos[right.Party]);
                if (result == 0)
                {
                    result = left.Votes.CompareTo(right.Votes);
                    if (result == 0)
                    {
                        result = rnd.Next(1) == 1 ? 1 : -1;
                    }
                }
                return result;
            });

            for (int i = 0; i < mandatesToTakeAway; i++)
            {
                losingMandate[i % losingMandate.Count].Mandates -= 1;
            }
        }

        private void AssignRemainingMandates(TotalResults results, int remainingMandates, IDictionary<Party, int> partyModulos, HashSet<Party> partiesWithEnoughCandidates)
        {
            Random rnd = new Random();
            List<PartyResults> gainingMandate = new List<PartyResults>();

            foreach (var gaining in results.PartyResults.Values)
            {
                if (gaining.PassedQuorum)
                {
                    gainingMandate.Add(gaining);
                }
            }

            gainingMandate.Sort((left, right) => {
                int result = partyModulos[right.Party].CompareTo(partyModulos[left.Party]);
                if (result == 0)
                {
                    result = right.RemainingVotes.CompareTo(left.RemainingVotes);
                    if (result == 0) 
                    {
                        result = right.Votes.CompareTo(left.Votes);
                        if (result == 0)
                        {
                            result = rnd.Next(1) == 1 ? 1 : -1;
                        }
                    }
                }
                return result;
            });

            for (int i = 0; i < remainingMandates; i++)
            {
                gainingMandate[i].Mandates += 1;
            }
        }

        private void UpdateRepublicVoteNumber(TotalResults results)
        {
            int remainingMandates = 200;
            int transferredVotes = 0;

            foreach (var partyResutls in results.PartyResults.Values)
            {
                if (partyResutls.PassedQuorum)
                {
                    remainingMandates -= partyResutls.Mandates;
                    transferredVotes += partyResutls.RemainingVotes;
                }
            }

            results.VoteNumber = (int)Math.Round((double)transferredVotes / (remainingMandates + 1));
        }

        private void UpdateRepublicResults(TotalResults results)
        {
            // propagate region results to the central (republic-level) results
            foreach (var regionResults in results.RegionResults.Values)
            {
                foreach (var partyResutls in regionResults.PartyResults.Values)
                {
                    if (partyResutls.PassedQuorum)
                    {
                        results.PartyResults[partyResutls.Party].RemainingVotes += partyResutls.RemainingVotes;
                        results.PartyResults[partyResutls.Party].Mandates += partyResutls.Mandates;
                    }
                }
            }
        }

        private void CalculateFirstScrutiny(TotalResults results)
        {
            foreach (var regionResults in results.RegionResults.Values)
            {
                int mandatesGiven = 0;
                foreach (var partyResults in regionResults.PartyResults.Values)
                {
                    if (partyResults.PassedQuorum)
                    {
                        partyResults.Mandates = partyResults.Votes / regionResults.VoteNumber;
                        partyResults.RemainingVotes = partyResults.Votes % regionResults.VoteNumber;

                        mandatesGiven += partyResults.Mandates;
                    }
                }

                if (mandatesGiven > regionResults.Mandates)
                {
                    Random rnd = new Random();
                    List<PartyResults> yieldingMandate = new List<PartyResults>();
                    foreach (var yielding in regionResults.PartyResults.Values)
                    {
                        if (yielding.PassedQuorum)
                        {
                            yieldingMandate.Add(yielding);
                        }
                    }

                    yieldingMandate.Sort((left, right) => {
                        int result = left.RemainingVotes.CompareTo(right.RemainingVotes);
                        if (result == 0) 
                        {
                            result = left.Votes.CompareTo(right.Votes);
                            if (result == 0)
                            {
                                result = rnd.Next(1) == 1 ? 1 : -1;
                            }
                        }
                        return result;
                    });

                    for (int i = 0; i < mandatesGiven - regionResults.Mandates; i++)
                    {
                        yieldingMandate[i].Mandates -= 1;
                    }
                }

                foreach (var partyResults in regionResults.PartyResults.Values)
                {
                    if (partyResults.PassedQuorum)
                    {
                        if (partyResults.Mandates == 0)
                        {
                            // make sure that if we took away a mandate from a party that resulted in
                            // zero mandates in the given region, we set the RemainingVotes back to the
                            // original votes for the second scrutiny
                            partyResults.RemainingVotes = partyResults.Votes;
                        }

                        // make sure we don't assign more mandates than there is candidates for the given party
                        // any revoked mandates will be assigned during the second scrutiny
                        int numCandidates = partyData.GetNumCandidatesInRegion(regionResults.Region.Id, partyResults.Party.Id);
                        if (partyResults.Mandates > numCandidates)
                        {
                            partyResults.Mandates = numCandidates;
                        }
                    }
                }
            }
        }

        private void UpdateRegionVoteNumbers(TotalResults results)
        {
            foreach (var regionResults in results.RegionResults.Values)
            {
                int totalQuoredPartyVotes = 0;

                foreach (var partyResults in regionResults.PartyResults.Values)
                {
                    if (partyResults.PassedQuorum)
                    {
                        totalQuoredPartyVotes += partyResults.Votes;
                    }
                }

                regionResults.VoteNumber = (int)Math.Round((double)totalQuoredPartyVotes / (regionResults.Mandates + 2));
            }
        }

        private void ApplyQuorum(TotalResults results)
        {
            List<Party> failedParties = new List<Party>();
            int quorumAdjustment = -1;
            
            do
            {
                failedParties.Clear();
                quorumAdjustment++;

                foreach (PartyResults partyResults in results.PartyResults.Values)
                {
                    int quorum = partyData.GetQuorum(partyData.GetPartyComposition(partyResults.Party.Id)) - quorumAdjustment;
                    if (quorum > partyResults.Percentage)
                    {
                        failedParties.Add(partyResults.Party);
                    }
                }
            } while ((results.PartyResults.Keys.Count - failedParties.Count) < 2);

            foreach (var failedParty in failedParties)
            {
                results.PartyResults[failedParty].PassedQuorum = false;

                foreach (var regionResult in results.RegionResults.Values)
                {
                    if (regionResult.PartyResults.ContainsKey(failedParty))
                    {
                        regionResult.PartyResults[failedParty].PassedQuorum = false;
                    }   
                }
            }
        }

        private void UpdateRegionMandates(TotalResults results)
        {
            var regionModulos = new Dictionary<Region, int>();

            int mandatesToGive = 200;
            foreach (var regionResults in results.RegionResults.Values)
            {
                regionResults.Mandates = regionResults.Votes / results.MandateNumber;
                regionModulos[regionResults.Region] = regionResults.Votes % results.MandateNumber;

                mandatesToGive -= regionResults.Mandates;
            }

            Random rnd = new Random();
            while (mandatesToGive > 0)
            {
                // not very efficient
                int maxModulo = int.MinValue;
                IList<Region> candidateRegions = new List<Region>();
                foreach (var regionModulo in regionModulos)
                {
                    if (regionModulo.Value > maxModulo)
                    {
                        maxModulo = regionModulo.Value;
                        candidateRegions.Clear();
                        candidateRegions.Add(regionModulo.Key);
                    }
                    else if (regionModulo.Value == maxModulo)
                    {
                        candidateRegions.Add(regionModulo.Key);
                    }
                }

                Region regionToGetRemainingMandate = candidateRegions[rnd.Next(candidateRegions.Count)];
                results.RegionResults[regionToGetRemainingMandate].Mandates += 1;
                regionModulos.Remove(regionToGetRemainingMandate);

                mandatesToGive--;
            }

            int givenMandates = 0;
            foreach (var regionMandate in results.RegionResults.Values)
            {
                givenMandates += regionMandate.Mandates;
            }

            if (givenMandates != 200)
            {
                throw new Exception("Invalid number of mandates given!");
            }
        }

        private void UpdateRepublicMandateNumber(TotalResults results)
        {
            results.MandateNumber = (int)(Math.Round(results.Votes / 200.0));
        }

        private TotalResults LoadResults(XmlDocument resultsXml)
        {
            var resultsNode = resultsXml.SelectSingleNode("/VYSLEDKY");
            if (resultsNode == null)
            {
                throw new Exception("Invalid XML - Missing VYSLEDKY element!");
            }

            DateTime timestamp = XmlConvert.ToDateTime(resultsNode.Attributes["DATUM_CAS_GENEROVANI"].Value, XmlDateTimeSerializationMode.Local);

            var participationXml = resultsXml.SelectSingleNode("/VYSLEDKY/CR/UCAST");
            if (participationXml == null)
            {
                throw new Exception("Invalid XML - Missing UCAST element!");
            }
            
            int totalValidVotes = 0;
            if (!int.TryParse(participationXml.Attributes["PLATNE_HLASY"].Value, out totalValidVotes)
                || totalValidVotes < 0)
            {
                throw new Exception($"Invalid data - Total votes negative!");
            }

            decimal areasCompleted = XmlConvert.ToDecimal(participationXml.Attributes["OKRSKY_ZPRAC_PROC"].Value);
            if (areasCompleted < Decimal.Zero || areasCompleted > 100m)
            {
                throw new Exception($"Invalid data - Areas completed {areasCompleted} is out of range!");
            }

            decimal participation = XmlConvert.ToDecimal(participationXml.Attributes["UCAST_PROC"].Value);
            if (participation < Decimal.Zero || participation > 100m)
            {
                throw new Exception($"Invalid data - Participation {participation} is out of range!");
            }

            TotalResults results = new TotalResults(totalValidVotes, areasCompleted, participation, timestamp);
            LoadTotalPartyResults(resultsXml, results);
            LoadRegionResults(resultsXml, results);

            return results;
        }

        private void LoadRegionResults(XmlDocument resultsXml, TotalResults results)
        {
            foreach (XmlNode regionXml in resultsXml.SelectNodes("VYSLEDKY/KRAJ"))
            {
                int regionNumber = 0;
                int.TryParse(regionXml.Attributes["CIS_KRAJ"].Value, out regionNumber);
                if (regionNumber == 0)
                {
                    throw new Exception("Invalid results - invalid region number");
                }

                var regionName = regionXml.Attributes["NAZ_KRAJ"].Value;

                XmlNode regionValidVotesXml = regionXml.SelectSingleNode("UCAST");
                int regionValidVotes = 0;
                if (regionValidVotesXml == null
                    || !int.TryParse(regionValidVotesXml.Attributes["PLATNE_HLASY"].Value, out regionValidVotes)
                    || regionValidVotes < 0)
                {
                    throw new Exception($"Invalid data - {regionName} has invalid valid votes!");
                }

                var regionResults = new RegionResults(new Region(regionNumber, regionName), regionValidVotes);

                foreach (XmlNode regionPartyXml in regionXml.SelectNodes("STRANA"))
                {
                    int partyId = 0;
                    int.TryParse(regionPartyXml.Attributes["KSTRANA"].Value, out partyId);
                    if (partyId == 0)
                    {
                        throw new Exception("Invalid results - invalid party number");
                    }

                    var partyName = regionPartyXml.Attributes["NAZ_STR"].Value;

                    var regionPartyValues = regionPartyXml.SelectSingleNode("HODNOTY_STRANA");
                    if (regionPartyValues == null)
                    {
                        throw new Exception($"Invalid party {partyId} values in region {regionNumber}");
                    }

                    int regionPartyVotes = 0;
                    if (!int.TryParse(regionPartyValues.Attributes["HLASY"].Value, out regionPartyVotes) || regionPartyVotes < 0)
                    {
                        throw new Exception($"Invalid votes {regionPartyVotes} for party {partyId} in region {regionNumber}");
                    }

                    regionResults.AddPartyResults(new PartyResults(new Party(partyId, partyName), regionPartyVotes, Decimal.Zero));
                }

                results.AddRegionResults(regionResults);
            }
        }

        private void LoadTotalPartyResults(XmlDocument resultsXml, TotalResults results)
        {
            foreach (XmlNode totalPartyResults in resultsXml.SelectNodes("VYSLEDKY/CR/STRANA"))
            {
                int partyId = 0;
                if (!int.TryParse(totalPartyResults.Attributes["KSTRANA"].Value, out partyId) || partyId <= 0)
                {
                    throw new Exception("Invalid data - failed to parse KSTRANA");
                }
                
                var partyName = totalPartyResults.Attributes["NAZ_STR"].Value;

                var partyValuesXml = totalPartyResults.SelectSingleNode("HODNOTY_STRANA");
                if (partyValuesXml == null)
                {
                    throw new Exception($"Invalid data - failed to read HODNOTY_STRANA for {partyId}");
                }

                decimal partyPercentage = XmlConvert.ToDecimal(partyValuesXml.Attributes["PROC_HLASU"].Value);
                if (partyPercentage < decimal.Zero || partyPercentage > 100m)
                {
                    throw new Exception($"Invalid data - party percentage {partyPercentage} for {partyName} is out of range!");
                }

                int partyTotalVotes = 0;
                if (!int.TryParse(partyValuesXml.Attributes["HLASY"].Value, out partyTotalVotes) || partyTotalVotes < 0)
                {
                    throw new Exception($"Invalid data - party total votes {partyTotalVotes} is out of range!");
                }

                results.AddPartyResults(new PartyResults(new Party(partyId, partyName), partyTotalVotes, partyPercentage));
            }
        }

        private void ValidateResultsData(TotalResults results)
        {
            int totalValidVotes = results.Votes;

            foreach (var regionResults in results.RegionResults.Values)
            {
                totalValidVotes -= regionResults.Votes;
            }

            foreach (var partyResults in results.PartyResults.Values)
            {
                totalValidVotes += partyResults.Votes;
            }

            if (totalValidVotes != results.Votes)
            {
                throw new Exception("Invalid number of valid votes!");
            }
        }
        private IPartyDataProvider partyData;
    }
}