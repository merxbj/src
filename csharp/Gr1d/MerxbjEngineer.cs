using System.Collections.Generic;
using Gr1d.Api.Agent;
using Gr1d.Api.Deck;
using Gr1d.Api.Skill;
using Gr1d.Api.Node;

namespace Gr1d
{
    public class MerxbjEngineer : IEngineer1
    {
        private IDeck deck;
        private INodeInformation currentNode;
        private List<INodeInformation> visited;

        public void Initialise(IDeck deck)
        {
            this.deck = deck;
            this.visited = new List<INodeInformation>();
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

            if (currentNode == null)
            {
                currentNode = agentUpdate.Node;
                visited.Add(currentNode);
            }

            if (!currentNode.Owner.Equals(agentUpdate.Owner))
            {
                if (currentNode.IsClaimable)
                {
                    this.Claim(currentNode);
                }
                else
                {
                    
                }
            }
            else
            {
                deck.Trace("Current node already owned by this agent!", TraceType.Warning);
            }

            if (currentNode != null)
            {
                foreach (INodeInformation node in currentNode.Exits.Values)
                {
                    if (node.Sector.Equals(currentNode.Sector) && !this.visited.Contains(node))
                    {
                        // be conservative - walk through nodes only in the same sector - for now :-)
                        this.Move(node);
                        this.currentNode = node;
                        this.visited.Add(currentNode);
                        return; // we have just moved about - lets wait for another tick
                    }
                }
            }

        }

        public void OnAttacked(IAgentInfo attacker, IAgentUpdateInfo agentUpdate)
        {
            this.deck.Trace("Agent attacked by {0}!", TraceType.Warning, attacker.ToString());
        }

        public void OnArrived(IAgentInfo arriver, IAgentUpdateInfo agentUpdate)
        {
            this.deck.Trace("Arrived {0} to agent!", TraceType.Warning, arriver.ToString());
        }
    }
}
