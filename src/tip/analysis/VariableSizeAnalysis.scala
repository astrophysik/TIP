package tip.analysis


import tip.cfg._
import tip.ast.AstNodeData.DeclarationData
import tip.lattices.IntervalLattice
import tip.lattices.IntervalLattice._
import tip.solvers._


trait VariableSizeAnalysisWidening extends IntervalAnalysisWidening {
  /**
   * Type values borders
   */
  override val B = Set[Num](
    0, 1,
    Byte.MinValue, Byte.MaxValue,
    (Short.MaxValue + 1) * 2,
    Int.MinValue, Int.MaxValue,
    MInf, PInf
  )
}


object VariableSizeAnalysis {

  object Intraprocedural {

    /**
     * Variable Size Analysis, using the worklist solver with init and widening.
     */
    class WorklistSolverWithWidening(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
      extends IntraprocValueAnalysisWorklistSolverWithReachability(cfg, IntervalLattice)
        with WorklistFixpointSolverWithReachabilityAndWidening[CfgNode]
        with VariableSizeAnalysisWidening

    /**
     * Variable Size Analysis, using the worklist solver with init, widening, and narrowing.
     */
    class WorklistSolverWithWideningAndNarrowing(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
      extends IntraprocValueAnalysisWorklistSolverWithReachability(cfg, IntervalLattice)
        with WorklistFixpointSolverWithReachabilityAndWideningAndNarrowing[CfgNode]
        with VariableSizeAnalysisWidening {

      val narrowingSteps = 5
    }
  }
}
