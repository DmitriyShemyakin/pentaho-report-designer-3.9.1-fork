package org.pentaho.reporting.engine.classic.core.layout.process;

/**
 * Todo: Document me!
 * <p/>
 * Date: 23.09.2009
 * Time: 18:23:26
 *
 * @author Thomas Morgner.
 */
public class PageableBreakContext
{
  private long shift;
  private long initialShift;
  private long appliedShift;
  private boolean breakSuspended;

  public PageableBreakContext()
  {
  }

  public PageableBreakContext(final PageableBreakContext parent,
                              final boolean useInitialShift)
  {
    updateFromParent(parent, useInitialShift);
  }

  public void updateFromParent(final PageableBreakContext parent,
                               final boolean useInitialShift)
  {
    if (useInitialShift)
    {
      this.shift = parent.appliedShift;
      this.appliedShift = parent.appliedShift;
      this.initialShift = parent.appliedShift;
    }
    else
    {
      this.shift = parent.shift;
      this.appliedShift = parent.shift;
      this.initialShift = parent.shift;
    }
    this.breakSuspended = parent.breakSuspended;
  }

  public long getShift()
  {
    return shift;
  }

  public void setShift(final long shift)
  {
    this.shift = shift;
  }

  public long getAppliedShift()
  {
    return appliedShift;
  }

  public void setAppliedShift(final long appliedShift)
  {
    this.appliedShift = appliedShift;
  }

  public long getInitialShift()
  {
    return initialShift;
  }

  public boolean isBreakSuspended()
  {
    return breakSuspended;
  }

  public void suspendBreaks()
  {
    breakSuspended = true;
  }

  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("PageableBreakContext");
    sb.append("{shift=").append(shift);
    sb.append(", initialShift=").append(initialShift);
    sb.append(", breakSuspended=").append(breakSuspended);
    sb.append('}');
    return sb.toString();
  }
}
