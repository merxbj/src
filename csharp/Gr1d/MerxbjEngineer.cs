using Gr1d.Api.Agent;
using Gr1d.Api.Deck;
using Gr1d.Api.Skill;

namespace Gr1d
{
    public class MerxbjEngineer : IEngineer1
    {
        private IDeck deck;

        public void Initialise(IDeck deck)
        {
            this.deck = deck;
            this.deck.Trace("Hello Gr1d!", TraceType.Information);
        }

        public void Tick(IAgentUpdateInfo agentUpdate)
        {
            deck.Trace("+ MerxbjEngineer::Tick", TraceType.Information);
            this.Wait();
        }

        public void OnAttacked(IAgentInfo attacker, IAgentUpdateInfo agentUpdate)
        {
            deck.Trace("+ MerxbjEngineer::OnAttacked", TraceType.Information);
        }

        public void OnArrived(IAgentInfo arriver, IAgentUpdateInfo agentUpdate)
        {
            deck.Trace("+ MerxbjEngineer::OnArrived", TraceType.Information);
        }
    }
}
