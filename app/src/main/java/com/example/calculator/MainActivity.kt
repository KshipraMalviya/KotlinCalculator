package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    private var addOperation = false
    private var addDecimal = true

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View)
    {
        if (view is Button)
        {
            if (view.text == ".")
            {
                if (addDecimal)
                    binding.working.append(view.text)
                addDecimal = false
            } else
                binding.working.append(view.text)
            addOperation = true
        }
    }

    fun operationAction(view: View)
    {
        if(view is Button && addOperation)
        {
            binding.working.append(view.text)
            addOperation = false
            addDecimal = true
        }
    }

    fun allClearAction(view: View)
    {
        binding.working.text = ""
        binding.result.text = ""
    }

    fun backspaceAction(view: View)
    {
        val length = binding.working.length()
        if(length > 0)
        {
            binding.working.text = binding.working.text.substring(0, length-1)
        }
    }

    fun equalsAction(view: View)
    {
        binding.result.text = calculateResults()
    }

    private fun calculateResults(): String
    {
        val operators = digitsOperator()
        if(operators.isEmpty())
            return ""
        val timesDivision = timesDivisionCalculate(operators)
        if(timesDivision.isEmpty())
            return ""
        val result = addSubtract(timesDivision)
        return result.toString()
    }

    private fun addSubtract(passedList: MutableList<Any>): Float
    {
        var result = passedList[0] as Float
        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.size-1)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if(operator == '+')
                    result += nextDigit
                if(operator == '-')
                    result -= nextDigit
            }
        }
        return result
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != (passedList.size-1) && i<restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if(i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while(list.contains('x') || list.contains('/'))
        {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun digitsOperator() : MutableList<Any>
    {
        var list = mutableListOf<Any>()
        var currDigit = ""
        for(char in binding.working.text)
        {
            if(char.isDigit() || char == '.')
                currDigit += char
            else
            {
                list.add(currDigit.toFloat())
                currDigit = ""
                list.add(char)
            }
        }
        if(currDigit != "")
            list.add(currDigit.toFloat())
        return list
    }
}