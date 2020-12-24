package com.mirahome.tetrisgame.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import com.mirahome.tetrisgame.constant.BlockType
import com.mirahome.tetrisgame.constant.Constants.DEFAULT_MARGIN
import com.mirahome.tetrisgame.constant.Constants.GRID_LINE_WIDTH
import com.mirahome.tetrisgame.constant.Constants.SIMPLE_GRID_NUM
import java.util.*

class GameView : View {

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mGridWidth: Int = 0
    private var mMargin: Int = 0
    private var mDownIndex = 0
    private var mBlockMaxY = 0
    private var mGirdXNum = 0
    private var mGirdYNum = 0
    private var mBottomLayerY = 0
    private var mTempBlockHorizontalOffset = 0
    private var mRemoveLineCount = 0

    private var mPaint: Paint? = null

    private var mGameStatusListener: GameStatusListener? = null
    private var mTempBlock: LinkedList<Point> = LinkedList()
    private var mPileBlock: LinkedList<Point> = LinkedList()
    private var mTempBlockArray: Array<Array<Int>> = Array(3) { Array(3) { 0 } }
    private var mTotalBlockArray: Array<Array<Int>> = Array(mGirdYNum) { Array(mGirdXNum) { 0 } }
    private val mRandom = Random()
    private var mCurrentBlockType: BlockType? = null
    private var mNextBlockType: BlockType? = null

    constructor(context: Context) : super(context) {
        initData()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initData()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initData()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(resolveMeasure(widthMeasureSpec, mWidth), resolveMeasure(heightMeasureSpec, mHeight))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBlock(canvas!!)
    }

    fun setGameStatusListener(listener: GameStatusListener) {
        mGameStatusListener = listener
    }

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWidth = wm.defaultDisplay.width
        mHeight = mWidth

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.strokeWidth = GRID_LINE_WIDTH
        mPaint?.color = Color.parseColor("#4CAF50")

        mGirdYNum = SIMPLE_GRID_NUM
        mGirdXNum = (mGirdYNum * 0.7).toInt()
        mBottomLayerY = mGirdYNum - 1
        mTempBlockHorizontalOffset = mGirdXNum / 2

        mGridWidth = (mWidth - DEFAULT_MARGIN) / mGirdYNum
        mMargin = (mWidth - mGridWidth * mGirdYNum) / 2
        mTotalBlockArray = Array(mGirdYNum) { Array(mGirdXNum) { 0 } }
    }

    private fun initData() {
        initBlockType()
        initTotalBlock()
        initTempBlock()
        updateTempBlock()
        updateBlockMaxY()
    }

    private fun resolveMeasure(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
        val specSize = MeasureSpec.getSize(measureSpec)
        result = when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> defaultSize
            MeasureSpec.AT_MOST -> Math.min(specSize, defaultSize)
            else -> defaultSize
        }
        return result
    }

    private fun drawBlock(canvas: Canvas) {
        drawSingleBlock(canvas, mTempBlock)
        drawSingleBlock(canvas, mPileBlock)
    }

    private fun drawSingleBlock(canvas: Canvas, list: List<Point>) {
        for (it in list.iterator()) {
            val block = Rect()
            block.left = mGridWidth * it.x + mMargin + 1
            block.right = block.left + mGridWidth - 1
            block.top = mGridWidth * it.y + mMargin + 1
            block.bottom = block.top + mGridWidth - 1
            mPaint?.let {
                canvas.drawRect(block, it)
            }
        }
    }

    private fun initBlockType() {
        val currentBlockIndex = mRandom.nextInt(BlockType.values().size)
        val nextBlockIndex = mRandom.nextInt(BlockType.values().size)
        mCurrentBlockType = BlockType.values().get(currentBlockIndex)
        mNextBlockType = BlockType.values().get(nextBlockIndex)
    }

    private fun initTempBlock() {
        val index = mRandom.nextInt(BlockType.values().size)
        when (BlockType.values()[index]) {
            BlockType.TYPE_S_L -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 1
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_S_R -> {
                mTempBlockArray[0][0] = 0
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 1
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 1
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_L_L -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 1
                mTempBlockArray[1][1] = 0
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 1
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_L_R -> {
                mTempBlockArray[0][0] = 0
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 1
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 0
                mTempBlockArray[1][2] = 1
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 1
            }
            BlockType.TYPE_T -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 1
                mTempBlockArray[0][2] = 1
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 0
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_O -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 1
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 1
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 0
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_I -> {
                mTempBlockArray[0][0] = 0
                mTempBlockArray[0][1] = 1
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
        }

        mBlockMaxY = 0
        mDownIndex = 0
        mTempBlockHorizontalOffset = mGirdXNum / 2
    }

    private fun initTotalBlock() {
        for (i in mTotalBlockArray.indices) {
            val array = mTotalBlockArray[i]
            for (j in array.indices) {
                mTotalBlockArray[i][j] = 0
            }
        }
    }

    private fun updateTempBlock() {
        mTempBlock.clear()
        for (i in mTempBlockArray.indices) {
            val array = mTempBlockArray[i]
            for (j in array.indices) {
                val value = array[j]
                if (value != 0) {
                    val point = Point(j + mTempBlockHorizontalOffset, i + mDownIndex)
                    mTempBlock.add(point)
                }
            }
        }
    }

    private fun turn() {
        val temp: Array<Array<Int>> = Array(3) { Array(3) { 0 } }
        val len = temp.size
        for (i in mTempBlockArray.indices) {
            val array = mTempBlockArray[i]
            for (j in array.indices) {
                temp[j][len - 1 - i] = mTempBlockArray[i][j]
            }
        }

        for (i in mTempBlockArray.indices) {
            val array = mTempBlockArray[i]
            for (j in array.indices) {
                mTempBlockArray[i][j] = temp[i][j]
            }
        }
    }

    private fun checkAtBorder(isLeftBorder: Boolean): Boolean {
        val border = if (isLeftBorder) 0 else mGirdXNum - 1
        for (point in mTempBlock) {
            if (point.x == border) {
                return true
            }
        }
        return false
    }

    fun turnBlock() {
        turn()
        updateTempBlock()
        updateBlockMaxY()
        invalidate()
    }

    fun moveLeft() {
        if (checkAtBorder(true)) return
        for (point in mTempBlock) {
            point.x--
        }
        mTempBlockHorizontalOffset--
        invalidate()
    }

    fun moveRight() {
        if (checkAtBorder(false)) return
        for (point in mTempBlock) {
            point.x++
        }
        mTempBlockHorizontalOffset++
        invalidate()
    }

    fun downBlock() {
        mBlockMaxY++
        mDownIndex++

        for (point in mTempBlock) {
            point.y++
        }

        val isToBottom = checkToBottom()
        if (isToBottom) {
            val addBlock = deepCopyTempBlock()
            mPileBlock.addAll(addBlock)

            for (point in addBlock) {
                mTotalBlockArray[point.y][point.x] = 1
            }

            initTempBlock()
            updateTempBlock()
            updateBlockMaxY()
            updateCallback()

            checkRemoveBottomLine()
        }

        invalidate()
    }

    private fun checkFullBottomLine(): Boolean {
        val array = mTotalBlockArray[mGirdYNum - 1]
        for (i in array) {
            if (i == 0) {
                return false
            }
        }
        return true
    }


    private fun checkRemoveBottomLine() {
        if (checkFullBottomLine()) {
            val array = mTotalBlockArray[mGirdYNum - 1]
            for (i in array.indices) {
                array[i] = 0
            }

            val removeBlockPointList: LinkedList<Point> = LinkedList()
            for (point in mPileBlock) {
                if (point.y == mBottomLayerY) {
                    removeBlockPointList.add(point)
                }
            }
            mPileBlock.removeAll(removeBlockPointList)

            for (point in mPileBlock) {
                point.y++
            }

            val len = mTotalBlockArray.size - 1
            for (i in len downTo 1) {
                mTotalBlockArray[i] = mTotalBlockArray[i - 1]
            }

            mRemoveLineCount++
            checkRemoveBottomLine()
        }
    }

    private fun updateCallback() {
        mGameStatusListener?.onBlockToBottom()
    }

    private fun checkToBottom(): Boolean {
        var isThreeLine = false
        for (i in mTempBlockArray[2]) {
            if (i != 0) {
                isThreeLine = true
                break
            }
        }

        val bottomLineIndex = if (isThreeLine) 2 else 1
        var tempBlockBottomArray = mTempBlockArray[bottomLineIndex]
        var bottomBlockSize = 0

        for (item in tempBlockBottomArray) {
            if (item != 0) bottomBlockSize++
        }

        val checkBlockPointList: LinkedList<Point> = LinkedList()
        var len = mTempBlock.size - 1
        var star = len - bottomBlockSize + 1
        for (index in star..len) {
            checkBlockPointList.add(mTempBlock.get(index))
        }

        var hasEnoughSpace = true
        val index = if (mBlockMaxY + 1 == mTotalBlockArray.size) mTotalBlockArray.size - 1 else mBlockMaxY + 1
        val firstArray = mTotalBlockArray[index]
        val secondArray = mTotalBlockArray[index - 1]

        for (point in checkBlockPointList) {
            if (firstArray[point.x] == 1) {
                hasEnoughSpace = false
                break
            }
        }

        if (hasEnoughSpace) {
            var middleLayerBlockSize = 0
            checkBlockPointList.clear()
            val tempBlockMiddleArray = mTempBlockArray[bottomLineIndex - 1]

            for (item in tempBlockMiddleArray) {
                if (item != 0) middleLayerBlockSize++
            }

            len = mTempBlock.size - bottomBlockSize - 1
            star = len - middleLayerBlockSize + 1
            for (index2 in star..len) {
                checkBlockPointList.add(mTempBlock[index2])
            }

            for (point in checkBlockPointList) {
                if (secondArray[point.x] == 1) {
                    return true
                }
            }
        }

        if (mBlockMaxY == mBottomLayerY && hasEnoughSpace) return true
        return !hasEnoughSpace
    }

    private fun deepCopyTempBlock(): LinkedList<Point> {
        val result: LinkedList<Point> = LinkedList()
        for (point in mTempBlock) {
            val tempPoint = Point(point.x, point.y)
            result.add(tempPoint)
        }
        return result
    }

    private fun updateBlockMaxY() {
        mBlockMaxY = 0
        for (point in mTempBlock) {
            if (point.y > mBlockMaxY) mBlockMaxY = point.y
        }
    }
}
