package tip.analysis

import tip.ast.{AAssignStmt, AIdentifier, AStmt, AVarStmt, NoRecords}
import tip.ast.AstNodeData.DeclarationData
import tip.cfg.{CfgFunExitNode, CfgNode, CfgStmtNode, IntraproceduralProgramCfg, ProgramCfg}
import tip.lattices.{MapLattice, PowersetLattice}
import tip.solvers.{SimpleMapLatticeFixpointSolver, SimpleWorklistFixpointSolver}

abstract class ReachingDefinitionsAnalysis(cfg: ProgramCfg)(implicit declData: DeclarationData) extends FlowSensitiveAnalysis(true) {

  val lattice : MapLattice[CfgNode, PowersetLattice[AAssignStmt]] = new MapLattice(new PowersetLattice())

  val domain: Set[CfgNode] = cfg.nodes

  NoRecords.assertContainsProgram(cfg.prog)

  def removeRefs(s: lattice.sublattice.Element, identifier: AIdentifier): lattice.sublattice.Element =
    s.filter(stmt => stmt.left match {
      case id: AIdentifier =>
        !id.name.equals(identifier.name)
      case _ => true
    })

  def transfer(n: CfgNode, s:lattice.sublattice.Element): lattice.sublattice.Element =
    n match {
      case r: CfgStmtNode =>
        r.data match {
          case as: AAssignStmt =>
            as.left match {
              case id: AIdentifier =>
                lattice.sublattice.lub(removeRefs(s, id), Set(as))
              case _ => s
            }
          case _ => s
        }
      case _ => s
    }
}

class ReachingDefAnalysisSimpleSolver(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
  extends ReachingDefinitionsAnalysis(cfg)
  with SimpleMapLatticeFixpointSolver[CfgNode]
  with ForwardDependencies

class ReachingDefAnalysisWorklistSolver(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
  extends ReachingDefinitionsAnalysis(cfg)
    with SimpleWorklistFixpointSolver[CfgNode]
    with ForwardDependencies
