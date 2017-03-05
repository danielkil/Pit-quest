package klerman.pitapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class hold point list which always sorted by X parameters. and draw them to the canvas.
 * In this way the edges will render beautifully.
 * Created by danie on 05/03/2017.
 */

public class Pit extends ViewGroup {
    private Paint mPaint = new Paint();  //to draw on canvas
    private Context mContext;
    private ArrayList<Point> mPoints = new ArrayList<Point>();  //list of points to draw in the canvas

    private int mInitCount = 5; //starting number of points
    private boolean mFirst = true;
    private int mSelectedPoint = -1;
    private int mBallRadius = 10;

    private int mCanvasWidth;
    private int mCanvasHeight;

    public Pit(Context context) {
        super(context);
        init(context);
    }

    public Pit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Pit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * initialization
     * @param context
     */
    private void init(Context context) {
        mContext = context;
    }

    /**
     * Position all children within this layout.
     * i implement it without children.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * set the custom view size
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    /**
     * The method that draws the lines and points.
     * This method will draw all the painting and update what we see.
     * This method activated by "invalidate();" method.
     * @param canvas - all the painting (lines, points) will be on the canvas
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        if(mFirst)  //At first time create five random points in the canvas
        {
            mCanvasWidth = canvas.getWidth();      //get canvas size
            mCanvasHeight = canvas.getHeight();

            Random random = new Random();
            Point myPoint;
            for(int i = 0 ; i < mInitCount ; i++)
            {
                myPoint = new Point(random.nextInt(canvas.getWidth()),random.nextInt(canvas.getHeight()));  //create new random point
                if(mPoints.size() == 0) {   //if first point, just add to the point list.
                    mPoints.add(myPoint);
                }
                else
                {
                    AddPointInSortedWay(myPoint);   // add not first point to list
                }
            }
            mFirst = false;
        }

        //Draw axis
        int axisColor = mContext.getResources().getColor(R.color.axis);
        mPaint.setColor(axisColor);
        mPaint.setStrokeWidth(5);
      //  public void drawLine(float startX, float startY, float stopX, float stopY,@NonNull Paint paint)
        canvas.drawLine(0, mCanvasHeight/2, mCanvasWidth, mCanvasHeight/2, mPaint);  // X axis
        canvas.drawLine(mCanvasWidth/2, 0, mCanvasWidth/2, mCanvasHeight, mPaint);   // Y axis
        //END Draw axis

        //Draw the points
        int pointColor = mContext.getResources().getColor(R.color.point);
        mPaint.setColor(pointColor);
        for(int i = 0 ; i < mPoints.size() ; i++){
            canvas.drawCircle(mPoints.get(i).x, mPoints.get(i).y, mBallRadius, mPaint);
        }
        //END Draw the points

        //Draw line between points
        int blueColor = mContext.getResources().getColor(R.color.blue);
        mPaint.setColor(blueColor);
        mPaint.setStrokeWidth(3);
        for(int i = 0 ; i < mPoints.size() -1 ; i++)// draw lines
        {
            canvas.drawLine(mPoints.get(i).x, mPoints.get(i).y, mPoints.get(i+1).x, mPoints.get(i+1).y, mPaint);  // line between points
        }
        //End Draw line between points
    }

    /**
     * this method will activate when there is interaction with the touch screen.
     * @param event - touch event with all the needed parameters
     * @return true to get continuous action.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction(); //get event action
        int X = (int)event.getX();      //get the X,Y coordinates of where the event happened on the screen
        int Y = (int)event.getY();

        switch (action) {

            case MotionEvent.ACTION_DOWN:   //check if point was pressed
          //      Log.d("PIT","Press");
                for(int i = 0 ; i < mPoints.size() ; i++)
                {
                    if((mPoints.get(i).x >= X - mBallRadius*2 && mPoints.get(i).x <= X + mBallRadius*2) &&      //check if inside the radius (i doubled the radius Because the fingers are very big Compared to the points)
                            (mPoints.get(i).y >= Y - mBallRadius*2 && mPoints.get(i).y <= Y + mBallRadius*2))
                    {
                        mSelectedPoint = i;  //if point was pressed, save the point.
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:   //check if moving finger on screen
          //      Log.d("PIT","Move");
                if(mSelectedPoint != -1)    //if the press action was on a point, move the point.
                {
                    if(X > 0 && X < mCanvasWidth && Y > 0 && Y < mCanvasHeight) //if touch in canvas area
                    {
                        mPoints.get(mSelectedPoint).set(X, Y);  //set new coordinates of pressed point
                        if (mSelectedPoint != 0) //if the point not first
                        {
                            if (mPoints.get(mSelectedPoint).x < mPoints.get(mSelectedPoint - 1).x) {   //if the selected point get over Her left Neighbor point
                                Collections.swap(mPoints, mSelectedPoint, mSelectedPoint - 1);      //keep the points sorted
                                mSelectedPoint--;
                            }
                        }
                        if (mSelectedPoint != mPoints.size() - 1)   //if point not last
                        {
                            if (mPoints.get(mSelectedPoint + 1).x < mPoints.get(mSelectedPoint).x) {    //if the selected point get over Her right Neighbor point
                                Collections.swap(mPoints, mSelectedPoint, mSelectedPoint + 1);      //swap and keep the points sorted
                                mSelectedPoint++;
                            }
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP: //if stop pressing the screen
                mSelectedPoint = -1;    //no point is selected
          //      Log.d("PIT","Release");
                break;
        }

        invalidate();   // redraw the canvas
        return true;
    }

    /**
     * Add new point to the Pit in 0,0 axis Coordinates
     */
    public void AddNewPoint()
    {
        Point newPoint = new Point(mCanvasWidth/2,mCanvasHeight/2); //add point in center of canvas

        AddPointInSortedWay(newPoint);
        invalidate();   // redraw the canvas
    }

    /**
     * Add new point to the Pit and keep the list sorted by X.
     * @param myPoint
     */
    private void AddPointInSortedWay(Point myPoint)
    {
        for(int j = 0 ; j < mPoints.size(); j++)
        {
            if(mPoints.get(j).x > myPoint.x) {  //Enter points in sorted way (sort by X)
                mPoints.add(j, myPoint);
                break;
            }
            if(j == mPoints.size() -1){ //if new point x component bigger then all existing points, set last
                mPoints.add(myPoint);
                break;
            }
        }
    }

    /**
     * i override this method to call him from the main activity,
     * cant find another way.
     * @param view - null
     */
    @Override
    public void addView(View view)
    {
        AddNewPoint();
        return;
    }
}
