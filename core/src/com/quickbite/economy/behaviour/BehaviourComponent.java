package com.quickbite.economy.behaviour;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class BehaviourComponent implements Component
{
	public Task currTask;

	private BlackBoard bb;
	public final BlackBoard getbb()
	{
		return bb;
	}
	private void setbb(BlackBoard value)
	{
		bb = value;
	}

    public Callback onCompletionCallback;
    public final Callback getonCompletionCallback()
    {
        return onCompletionCallback;
    }
    public final void setonCompletionCallback(Callback value)
    {
        onCompletionCallback = value;
    }

	public final boolean getidle()
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
	public final Task getcurrTask()
	{
		return currTask;
	}
	public final void setcurrTask(Task value)
	{
		currTask = value;
		currTask.getController().reset();
		currTask.getController().SafeStart();
	}

	public interface Callback
	{
		void invoke();
	}

	public final boolean CurrTaskDone()
	{
		return getcurrTask() == null || !getcurrTask().getController().getRunning();
	}
}