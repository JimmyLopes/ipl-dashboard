import { React, useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { MatchDetailCard } from '../components/MatchDetailCard';

export const MatchPage = () => {

  const [matches, setMatches] = useState([]);
  const { teamName, year } = useParams();

  useEffect(() => {
    const fetchMatches = async () => {
      const response = await fetch(`http://localhost:8080/team/${teamName}/matches?year=${year}`);
      const data = await response.json();
      setMatches(data);
    };
    fetchMatches();
  }, [teamName, year]);

  return (
    <div className="MatchPage">
      <div className="year-selector">
        <h3> Select Year </h3>
        {/* <YearSelector teamName={teamName} /> */}
      </div>
      <div className="MatchPage">
        <h1>MatchPage</h1>
        {matches.map(match => <MatchDetailCard teamName={teamName} match={match} />)}
      </div></div>
  );
}