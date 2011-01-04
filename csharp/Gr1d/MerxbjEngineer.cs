using System.Collections.Generic;
using Gr1d.Api.Agent;
using Gr1d.Api.Deck;
using Gr1d.Api.Skill;
using Gr1d.Api.Node;

namespace Gr1d
{
    public class MerxbjEngineer : IEngineer2
    {
        private IDeck deck;
        private INodeInformation currentNode;
        private List<INodeInformation> visited;
        private IEnumerator<INodeInformation> walker;

        public void Initialise(IDeck deck)
        {
            this.deck = deck;
            visited = new List<INodeInformation>();
        }

        public void Tick(IAgentUpdateInfo agentUpdate)
        {
            bool unitTestApplied = false;
            if (agentUpdate.Effects != null)
            {
                foreach (AgentEffect effect in agentUpdate.Effects)
                {
                    if (effect.Equals(AgentEffect.UnitTest))
                    {
                        unitTestApplied = true;
                    }
                }
            }

            if (!unitTestApplied)
            {
                this.UnitTest();
            }
            else
            {
                deck.Trace("UnitTest effect already applied. Will not extend ...", TraceType.Verbose);
            }

            if (currentNode == null)
            {
                deck.Trace("Current node was initialised for this agent!", TraceType.Verbose);
                currentNode = agentUpdate.Node;
                visited.Add(currentNode);
            }

            if (currentNode != null)
            {
                foreach (IAgentInfo agent in currentNode.OtherAgents)
                {
                    this.Pin(agent);
                    this.Attack(agent);
                }
            }

            if (currentNode != null && !currentNode.Owner.Equals(agentUpdate.Owner))
            {
                if (currentNode.IsClaimable)
                {
                    this.Claim(currentNode);
                }
                else
                {
                    deck.Trace("Agent met unclaimable node!", TraceType.Verbose);
                }
            }
            else
            {
                deck.Trace("No current node or already owned by this agent!", TraceType.Warning);
            }

            if (walker == null)
            {
                if (currentNode != null)
                {
                    walker = currentNode.Exits.Values.GetEnumerator();
                }
                else
                {
                    deck.Trace("Walker not initialised! Current node was not set.", TraceType.Critical);
                }
            }
            else
            {
                if (!walker.MoveNext() && currentNode != null)
                {
                    walker = currentNode.Exits.Values.GetEnumerator();
                }
                else
                {
                    deck.Trace("Agent is probably stuck on a single node!", TraceType.Critical);
                }
            }

            if (walker != null)
            {
                INodeInformation nextNode = walker.Current;
                while (nextNode == null)
                {
                    if(!walker.MoveNext() && currentNode != null)
                    {
                        walker = currentNode.Exits.Values.GetEnumerator();
                    }
                    else
                    {
                        deck.Trace("Agent is probably stuck on a single node!", TraceType.Critical);
                        break;
                    }
                }

                if (nextNode != null)
                {
                    this.Move(nextNode);
                    currentNode = nextNode;
                }
                else
                {
                    deck.Trace("Next node is null - will not move anywher!", TraceType.Error);
                }
            }

        }

        public void OnAttacked(IAgentInfo attacker, IAgentUpdateInfo agentUpdate)
        {
            this.Attack(attacker); // just fight back - don't know what else to do, now
        }

        public void OnArrived(IAgentInfo arriver, IAgentUpdateInfo agentUpdate)
        {
            if (arriver.Clan != agentUpdate.Clan)
            {
                if (arriver.Level >= agentUpdate.Level + 1)
                {
                    this.Attack(arriver);
                    this.Pin(arriver);
                }
                else
                {
                    deck.Trace("Agent met by another agent with too high level! Will not attack.", TraceType.Verbose);
                }
            }
            else
            {
                deck.Trace("Agent met by another agent from the same clan! Will not attack.", TraceType.Verbose);
            }
        }
    }
}
