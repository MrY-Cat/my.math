package my.math
//mathProblem.kt
import java.math.BigDecimal
import kotlin.math.absoluteValue
import kotlin.math.pow

/**
 * 集齐完备事件组期望试验次数.
 *
 * 即计算对n个事件构成的完备事件组，使每个事件都恰好至少发生一次所需的总试验次数r的期望E(r).
 * @param n 事件总数.
 * @param probabilities 每个事件的概率.
 * @param calculationAccuracyOfInfiniteSeries 计算E(r)=ΣrP(r)时，无穷级数计算的项数，不指定时默认为2/probabilities.min().
 * @exception Exception 概率数组probabilities应有n个值.
 * @exception Exception n个事件的概率均应属于0到1，且总和应为1.
 * @author qq2278010681
 */
fun gatherEventClusterEr(n:Int, vararg probabilities:Double,calculationAccuracyOfInfiniteSeries:Int?=null):BigDecimal
{
    if(probabilities.size!=n) throw Exception("概率数组probabilities应有n个值")
    if(probabilities.any{it<0.0||it>1.0}||(1.0-probabilities.sum()).absoluteValue>0.0000000001) throw Exception("n个事件的概率均应属于0到1，且总和应为1")
    //若未给出计算项数，无穷级数默认计算项数为：2/事件组中最小概率.
    @Suppress("LocalVariableName")
    val CAOIS= calculationAccuracyOfInfiniteSeries ?: (2/probabilities.min()).toInt().coerceAtLeast(30)
    var gatherEr= BigDecimal.ZERO
    for(r in n..CAOIS)
    {
        var pEr= BigDecimal.ZERO
        val splitMatrix=integerPartitionOrderly(r-1, n-1)//计算最内层循环所用到的拆分矩阵
        for( i in 1..n)
        {
            var pAi= BigDecimal.ZERO
            for(j in 1..C(r-2,n-2).toLong())
            {
                var pBj= BigDecimal.ONE
                for(k in 1..n)
                {
                    if(k==i) continue//第i个元素不在拆分范围内
                    val locate:Int= if(k<i) k else k-1
                    pBj*= BigDecimal.valueOf(probabilities[k-1].pow(splitMatrix[j.toInt(),locate]))
                }
                pBj*= groupingCombinationsOrderly(r-1,*splitMatrix[j.toInt()].toIntArray()).toBigDecimal()//有序分组种类数
                pAi+=pBj
            }
            pEr+= BigDecimal.valueOf(probabilities[i-1])*pAi
        }
        gatherEr+= BigDecimal.valueOf(r.toLong())*pEr
    }
    return gatherEr
}