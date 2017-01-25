package com.quickbite.economy.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.quickbite.economy.behaviour.BlackBoard;
import com.quickbite.economy.behaviour.Task;

public class BehaviourComponent implements Component
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

    public BehaviourComponent(Entity myEntity){
    	BlackBoard bb = new BlackBoard();
    	bb.myself = myEntity;

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
		currTask.getController().reset();
		currTask.getController().SafeStart();
	}

	public interface Callback
	{
		void invoke();
	}
}