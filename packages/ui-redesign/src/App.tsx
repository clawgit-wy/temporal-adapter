import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "@/components/Layout";
import Dashboard from "@/pages/Dashboard";
import WorkflowList from "@/pages/WorkflowList";
import WorkflowDetail from "@/pages/WorkflowDetail";
import Workers from "@/pages/Workers";
import Schedules from "@/pages/Schedules";
import Help from "@/pages/Help";

export default function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/workflows" element={<WorkflowList />} />
          <Route path="/workflows/:id" element={<WorkflowDetail />} />
          <Route path="/workers" element={<Workers />} />
          <Route path="/schedules" element={<Schedules />} />
          <Route path="/help" element={<Help />} />
        </Routes>
      </Layout>
    </Router>
  );
}
