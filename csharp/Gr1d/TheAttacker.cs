using System;
using Gr1d.Api.Agent;
using Gr1d.Api.Deck;

namespace Gr1d
{
    class TheAttacker : IEngineer2
    {
        private IDeck deck;

        public void Initialise(IDeck deck)
        {
            this.deck = deck;
        }

        public void Tick(IAgentUpdateInfo agentUpdate)
        {
            throw new NotImplementedException();
        }

        public void OnAttacked(IAgentInfo attacker, IAgentUpdateInfo agentUpdate)
        {
            throw new NotImplementedException();
        }

        public void OnArrived(IAgentInfo arriver, IAgentUpdateInfo agentUpdate)
        {
            throw new NotImplementedException();
        }
    }
}
