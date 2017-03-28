package com.quickbite.economy.components;


import com.badlogic.ashley.core.Entity;
import com.quickbite.economy.behaviour.BlackBoard;
import com.quickbite.economy.behaviour.Task;
import com.quickbite.economy.interfaces.MyComponent;
import org.jetbrains.annotations.NotNull;

public class BehaviourComponent implements MyComponent
{
	private Task currTask;

	private BlackBoard bb;
	public final BlackBoard getBlackBoard()
	{
		return bb;
	}
	private void setbb(BlackBoard value)
	{
		bb = value;
	}

    public Callback onCompletionCallback;
    public final Callback getOnCompletionCallback()
    {
        return onCompletionCallback;
    }
    public final void setOnCompletionCallback(Callback value)
    {
        onCompletionCallback = value;
    }

	public final boolean isIdle()
	{
		return currTask == null || !currTask.getController().getRunning();
	}

	public String[] currTaskName = new String[10];

    public BehaviourComponent(Entity entity){
    	BlackBoard bb = new BlackBoard();
    	bb.myself = entity;

		this.setbb(bb);
	}

	/**
	 Sets the current task. Handles resetting and starting the task.
	*/
	public final Task getCurrTask()
	{
		return currTask;
	}
	public final void setCurrTask(Task value)
	{
		currTask = value;
		currTask.getController().safeReset();
		currTask.getController().safeStart();
	}

    @Override
    public void initialize() {

    }

    @Override
    public void dispose(@NotNull Entity entity) {

    }

    public interface Callback
	{
		void invoke();
	}
}